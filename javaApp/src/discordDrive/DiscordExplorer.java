package discordDrive;

import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

@SuppressWarnings("serial")
public class DiscordExplorer extends AbstractExplorer {
	private static String ACTIONTEXT = "Download";

	// TODO
	public DiscordExplorer(String path) {
		super(path);
	}

	// TODO
	@Override
	protected DefaultMutableTreeNode createTree(String path) {
		return null;

	}

	// TODO
	@Override
	protected void fillTree(DefaultMutableTreeNode root, String filename, int depth) {

	}

	// TODO
	@Override
	protected JButton createActionButton() {
		return new JButton(ACTIONTEXT);
	}

	@Override
	protected ActionListener getRefreshListener() {
		return null;
	}

	@Override
	protected TreeSelectionListener showContents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected String getPath(TreePath tp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void showFiles(String path) {
		// TODO Auto-generated method stub

	}

	@Override
	protected JTree createJTree(DefaultMutableTreeNode top) {
		// TODO Auto-generated method stub
		return null;
	}
}
