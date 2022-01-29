package discordDrive;

import javax.swing.*;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.io.File;

public class DiscordExplorer extends AbstractExplorer {

    private static String REFRESHTEXT = "Refresh";
    private static String ACTIONTEXT = "Download";

    public DiscordExplorer(String path) {
        super(path);
    }

    // TODO
    @Override
    protected DefaultMutableTreeNode createTree(File temp) {
        return new DefaultMutableTreeNode();
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
}
