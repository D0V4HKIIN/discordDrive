package discordDrive;

import java.awt.BorderLayout;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

/***********************************/
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

	protected final String[] colHeads = { "File Name", "SIZE(in Bytes)" };
	String[][] data = { { "", "" } };

	public AbstractExplorer(String path) {
		pathField = new JTextField();
		action = createActionButton();

		jtb = new JTable(data, colHeads);
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

		pathField.addActionListener(getRefreshListener());

		showFiles(path);
	}

	// shows all the files inside path
	protected abstract void showFiles(String path);

	protected abstract String getPath(TreePath tp);

	protected abstract JTree createJTree(DefaultMutableTreeNode top);

	protected abstract DefaultMutableTreeNode createTree(String path) throws Exception;

	protected abstract void fillTree(DefaultMutableTreeNode root, String filename, int depth);

	protected abstract JButton createActionButton();

	protected abstract TreeSelectionListener showContents();

	protected abstract ActionListener getRefreshListener();
}
