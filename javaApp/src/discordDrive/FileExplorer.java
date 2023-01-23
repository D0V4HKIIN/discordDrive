package discordDrive;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

import discordInterface.DiscordInterface;

@SuppressWarnings("serial")
public class FileExplorer extends AbstractExplorer {
	public FileExplorer(String path, DiscordInterface dInterface) {
		super(path, dInterface);
	}

	private static String ACTIONTEXT = "Upload";

	public DefaultMutableTreeNode createTree(String path) throws Exception {
		// initialize tree
		File temp = new File(path);
		DefaultMutableTreeNode top = new DefaultMutableTreeNode(temp.getPath());
		if (!(temp.exists() && temp.isDirectory()))
			throw new Exception("path is not a folder");

		fillTree(top, temp.getPath(), 0);

		return top;

	}

	@Override
	public void fillTree(DefaultMutableTreeNode root, String filename, int depth) {
		if (depth == 2) {
			return;
		}

		File temp = new File(filename);

		if (!(temp.exists() && temp.isDirectory()))
			return;

		File[] filelist = temp.listFiles();

		if (filelist == null) {
			System.out.println("filelist is empty");
			return;
		}

		PriorityQueue<DefaultMutableTreeNode> fileNodesQueue = new PriorityQueue<DefaultMutableTreeNode>(
				new Comparator<DefaultMutableTreeNode>() {

					@Override
					public int compare(DefaultMutableTreeNode o1, DefaultMutableTreeNode o2) {
						return ((String) o1.getUserObject()).compareToIgnoreCase((String) o2.getUserObject());
					}

				});

		for (int i = 0; i < filelist.length; i++) {
			if (!filelist[i].isDirectory())
				continue;
			final DefaultMutableTreeNode tempDmtn = new DefaultMutableTreeNode(filelist[i].getName());
			fileNodesQueue.add(tempDmtn);
			final String newfilename = new String(filename + File.separator + filelist[i].getName());
//            Thread t = new Thread() {
//                public void run() {
			fillTree(tempDmtn, newfilename, depth + 1);
//                }//run
//            };//thread
//            if (t == null) {
//                System.out.println("no more thread allowed " + newfilename);
//                return;
//            }
//            t.start();
		}

		while (!fileNodesQueue.isEmpty()) {
			root.add(fileNodesQueue.poll());
		}
	}

	public JButton createActionButton() {
		return new JButton(ACTIONTEXT);
	}

	@Override
	public ActionListener getRefreshListener() {
		return new RefreshListener();
	}
	

	@Override
	protected ActionListener getButtonListener() {
		return new ButtonListener();
	}
	
	private class ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			dInterface.uploadFiles(getFilePaths(), "/data/");
		}
	}

	private class RefreshListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {

			DefaultMutableTreeNode newTop = null;
			try {
				newTop = createTree(getFolderPath());
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("You probably entered the path wrong.");
			}

			if (newTop != null)
				tree = new JTree(newTop);
			jsp.setViewportView(tree);
			showFiles(getFolderPath());
		}
	}

	@Override
	protected TreeSelectionListener showContents() {
		return new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				TreePath tp = e.getPath();
				if (tp == null)
					return;

				String s = getPath(tp);
				showFiles(s);
			}
		};

	}

	@Override
	protected String getPath(TreePath tp) {
		String s = tp.toString();
		s = s.replace("[", "");
		s = s.replace("]", "");
		s = s.replace(", ", File.separator);
		return s;
	}
	
	@Override
	protected String getFolderPath() {
		return pathField.getText();
	}
	
	@Override
	protected List<String> getFilePaths() {
		List<String> files = new ArrayList<String>();
		for (int index: jtb.getSelectedRows()){
			files.add(getFolderPath() + "/" + (String) jtb.getValueAt(index, 0));
		}
		return files;
	}

	@Override
	protected void showFiles(String path) {
		// set path in the text on top
		pathField.setText(path);

		File temp = new File(path);
		data = new String[][] { { "", "" } };

		if (!temp.exists())
			return;
		if (!temp.isDirectory())
			return;

		File[] filelist = temp.listFiles();
		int fileCounter = 0;
		data = new String[filelist.length][2];
		for (int i = 0; i < filelist.length; i++) {
			if (filelist[i].isDirectory())
				continue;
			data[fileCounter][0] = new String(filelist[i].getName());
			data[fileCounter][1] = new String(filelist[i].length() + "");
			fileCounter++;
		} // for

		String dataTemp[][] = new String[fileCounter][4];
		for (int k = 0; k < fileCounter; k++)
			dataTemp[k] = data[k];
		data = dataTemp;

		jtb = new JTable(data, colHeads);
		jspTable.setViewportView(jtb);
	}

	@Override
	protected JTree createJTree(DefaultMutableTreeNode top) {
		JTree jTree = new JTree(top);

		// show content of folder when clicked
		jTree.addTreeSelectionListener(showContents());

		// dynamically add nodes when tree gets opened
		jTree.addTreeWillExpandListener(new TreeWillExpandListener() {
			@Override
			public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
				TreePath eventPath = event.getPath();
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) eventPath.getLastPathComponent();
				node.removeAllChildren();
				fillTree(node, getPath(eventPath), 0);
			}

			@Override
			public void treeWillCollapse(TreeExpansionEvent event) throws ExpandVetoException {
				// TODO Auto-generated method stub

			}
		});
		return jTree;
	}
}