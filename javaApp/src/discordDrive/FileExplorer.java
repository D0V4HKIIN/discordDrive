package discordDrive;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import javax.swing.JButton;
import javax.swing.tree.DefaultMutableTreeNode;

import discordInterface.DiscordInterface;

@SuppressWarnings("serial")
public class FileExplorer extends AbstractExplorer {
	private static String ACTIONTEXT = "Upload";
	
	public FileExplorer(String path, DiscordInterface dInterface) {
		super(path, dInterface);
	}

	// TODO refactor with some generic file class, to use with discordFile
	@Override
	protected DefaultMutableTreeNode createTree(String path) throws Exception {
		// initialize tree
		File temp = new File(path);
		DefaultMutableTreeNode top = new DefaultMutableTreeNode(temp.getPath());
		if (!(temp.exists() && temp.isDirectory()))
			throw new Exception("path is not a folder");

		fillTree(top, temp.getPath(), 0);
		return top;
	}

	// TODO refactor with some generic file class, to use with discordFile
	@Override
	protected void fillTree(DefaultMutableTreeNode root, String filename, int depth) {
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
			fillTree(tempDmtn, newfilename, depth + 1);
		}

		while (!fileNodesQueue.isEmpty()) {
			root.add(fileNodesQueue.poll());
		}
	}

	
	@Override
	protected JButton createActionButton() {
		return new JButton(ACTIONTEXT);
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

	@Override
	protected String[][] getFilesData(String path){
		File temp = new File(path);
		
		if (!temp.exists())
			return new String[][] { { "", "" } };
		if (!temp.isDirectory())
			return new String[][] { { "", "" } };
		
		File[] filelist = temp.listFiles();
		List<String[]> tempFiles = new ArrayList<String[]>();
		for (int i = 0; i < filelist.length; i++) {
			if (filelist[i].isDirectory())
				continue;
			tempFiles.add(new String[] {new String(filelist[i].getName()), new String(filelist[i].length() + "")});
		}
		
		String[][] files = new String[tempFiles.size()][2];
		for(int i = 0; i < tempFiles.size(); i++)
			files[i] = tempFiles.get(i);
		
		return files;
	}
}