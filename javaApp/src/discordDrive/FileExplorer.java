package discordDrive;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Comparator;
import java.util.PriorityQueue;

import javax.swing.JButton;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class FileExplorer extends AbstractExplorer {
	private static String ACTIONTEXT = "Upload";

	public FileExplorer(String path) {
		super(path);
	}

	public DefaultMutableTreeNode createTree(File temp) {
		DefaultMutableTreeNode top = new DefaultMutableTreeNode(temp.getPath());
		if (!(temp.exists() && temp.isDirectory()))
			return top;

		fillTree(top, temp.getPath(), 0);

		return top;
	}

	@Override
	public void fillTree(DefaultMutableTreeNode root, String filename, int depth) {
		if (depth == 2) {
			return;
		}

		System.out.println("filling " + filename);

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

	private class RefreshListener implements ActionListener {
		public void actionPerformed(ActionEvent ev) {
			System.out.println("refreshed");
			File temp = new File(jtf.getText());
			DefaultMutableTreeNode newtop = createTree(temp);
			if (newtop != null)
				tree = new JTree(newtop);
			jsp.setViewportView(tree);
//            tree.addMouseListener(
//                    new MouseAdapter() {
//                        public void mouseClicked(MouseEvent me) {
//                            doMouseClicked(me);
//                        }
//                    });
			showFiles(jtf.getText());
		}
	}

	@Override
	protected TreeSelectionListener showContents() {
		return new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent e) {
				System.out.println("showing file");
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
}