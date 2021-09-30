package discordDrive;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;

public class FileExplorer extends AbstractExplorer {
    private static String ACTIONTEXT = "Upload";

    public FileExplorer(String path) {
        super(path);
    }

    public DefaultMutableTreeNode createTree(File temp) {
        DefaultMutableTreeNode top = new DefaultMutableTreeNode(temp.getPath());
        if (!(temp.exists() && temp.isDirectory()))
            return top;

        fillTree(top, temp.getPath());

        return top;
    }


    @Override
    public void fillTree(DefaultMutableTreeNode root, String filename) {
        File temp = new File(filename);

        if (!(temp.exists() && temp.isDirectory()))
            return;

        File[] filelist = temp.listFiles();

        for (int i = 0; i < filelist.length; i++) {
            if (!filelist[i].isDirectory())
                continue;
            final DefaultMutableTreeNode tempDmtn = new DefaultMutableTreeNode(filelist[i].getName());
            root.add(tempDmtn);
            final String newfilename = new String(filename + File.separator + filelist[i].getName());
            Thread t = new Thread() {
                public void run() {
                    fillTree(tempDmtn, newfilename);
                }//run
            };//thread
            if (t == null) {
                System.out.println("no more thread allowed " + newfilename);
                return;
            }
            t.start();
        }
    }


    public JButton createActionButton() {
        return new JButton(ACTIONTEXT);
    }

    @Override
    public ActionListener getRefreshListener() {
        return new RefreshListener();
    }

    public void doMouseClicked(MouseEvent me) {
        TreePath tp = tree.getPathForLocation(me.getX(), me.getY());
        if (tp == null) return;

        String s = tp.toString();
        s = s.replace("[", "");
        s = s.replace("]", "");
        s = s.replace(", ", File.separator);
        showFiles(s);
    }


    private class RefreshListener implements ActionListener {
        public void actionPerformed(ActionEvent ev) {
            File temp = new File(jtf.getText());
            DefaultMutableTreeNode newtop = createTree(temp);
            if (newtop != null)
                tree = new JTree(newtop);
            jsp.setViewportView(tree);
            tree.addMouseListener(
                    new MouseAdapter() {
                        public void mouseClicked(MouseEvent me) {
                            doMouseClicked(me);
                        }
                    });
            showFiles(jtf.getText());
        }
    }
}