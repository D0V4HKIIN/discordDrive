package discordDrive;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

import discordInterface.DiscordInterface;

/***********************************/
@SuppressWarnings("serial")
abstract class AbstractExplorer extends JPanel {
	// top text
	protected JTextField pathField;
	// left side tree
	protected JTree tree;
	// bottom button
	protected JButton action;
	// file view in middle
	protected JTable jtb;
	protected JScrollPane jsp;
	protected JScrollPane jspTable;
	
	protected DiscordInterface dInterface;

	protected final String[] colHeads = { "File Name", "SIZE(in Bytes)" };

	public AbstractExplorer(String path, DiscordInterface dInterface) {
		this.dInterface = dInterface;
		pathField = new JTextField();
		action = createActionButton();

		jtb = new JTable(new String[][] { { "", "" } }, colHeads);
		jspTable = new JScrollPane(jtb);

		try {
			DefaultMutableTreeNode top = createTree(path);
			tree = createJTree(top);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1);
		}

		// show panel to
		jsp = new JScrollPane(tree);

		// add to layout
		setLayout(new BorderLayout());
		add(pathField, BorderLayout.NORTH);
		add(jspTable, BorderLayout.CENTER);
		add(jsp, BorderLayout.WEST);
		add(action, BorderLayout.SOUTH);
		
		action.addActionListener(getButtonListener());

		pathField.addActionListener(getRefreshListener());

		showFiles(path);
	}

	protected String getPath(TreePath tp) {
		String s = tp.toString();
		s = s.replace("[", "");
		s = s.replace("]", "");
		s = s.replace(", ", File.separator);
		return s;
	}

	protected ActionListener getRefreshListener() {
		return new RefreshListener();
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

	protected String getFolderPath() {
		return pathField.getText();
	}
	
	protected List<String> getFilePaths() {
		List<String> files = new ArrayList<String>();
		for (int index: jtb.getSelectedRows()){
			files.add(getFolderPath() + "/" + (String) jtb.getValueAt(index, 0));
		}
		return files;
	}

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

	protected abstract DefaultMutableTreeNode createTree(String path) throws Exception;

	protected abstract void fillTree(DefaultMutableTreeNode root, String filename, int depth);

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

	protected abstract ActionListener getButtonListener();

	protected abstract JButton createActionButton();

	protected abstract String[][] getFilesData(String path);
	
	protected void showFiles(String path) {
		// set path in the text on top
		pathField.setText(path);
		
		String[][] data = getFilesData(path);

		jtb = new JTable(data, colHeads);
		jspTable.setViewportView(jtb);
	}
}
