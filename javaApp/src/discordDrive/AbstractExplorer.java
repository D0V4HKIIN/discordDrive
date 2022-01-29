package discordDrive;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

/***********************************/
abstract class AbstractExplorer extends JPanel {
	// top text
	protected JTextField jtf;
	// left side tree
	protected JTree tree;
	// bottom button
	protected JButton action;
	// file view in middle
	private JTable jtb;
	protected JScrollPane jsp;
	protected JScrollPane jspTable;

	private final String[] colHeads = { "File Name", "SIZE(in Bytes)" };
	String[][] data = { { "", "" } };

	public AbstractExplorer(String path) {
		jtf = new JTextField();
		action = createActionButton();

		File temp = new File(path);
		DefaultMutableTreeNode top = createTree(temp);

		tree = new JTree(top);

		jsp = new JScrollPane(tree);

		jtb = new JTable(data, colHeads);
		jspTable = new JScrollPane(jtb);

		setLayout(new BorderLayout());
		add(jtf, BorderLayout.NORTH);
		add(jsp, BorderLayout.WEST);
		add(jspTable, BorderLayout.CENTER);
		add(action, BorderLayout.SOUTH);

		tree.addTreeSelectionListener(showContents());
		tree.addTreeWillExpandListener(new TreeWillExpandListener() {
			@Override
			public void treeWillExpand(TreeExpansionEvent event) throws ExpandVetoException {
				// idk what to do with this
				// fillTree((DefaultMutableTreeNode) tree.getLastSelectedPathComponent(),
				// getPath(event.getPath()), 0);
				System.out.println("Expansion of the tree");
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

		jtf.addActionListener(getRefreshListener());
		showFiles(path);
	}

	// shows all the files inside path
	public void showFiles(String path) {
		// set path in the text on top
		jtf.setText(path);

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

	protected abstract String getPath(TreePath tp);

	protected abstract DefaultMutableTreeNode createTree(File temp);

	protected abstract void fillTree(DefaultMutableTreeNode root, String filename, int depth);

	protected abstract JButton createActionButton();

	protected abstract TreeSelectionListener showContents();

	protected abstract ActionListener getRefreshListener();
}
