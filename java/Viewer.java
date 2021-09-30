package discordDrive;

import javax.swing.*;

import java.awt.*;

import static javax.swing.WindowConstants.EXIT_ON_CLOSE;

public class Viewer {
    private JFrame frame = new JFrame("DiscordDrive");
    private FileExplorer local;
    private DiscordExplorer cloud;

    public Viewer(String path) {
        frame.setLayout(new GridLayout());
        // local files
        local = new FileExplorer(path);
        local.showFiles(path);
        frame.add(local, BorderLayout.WEST);

        // cloud files
        cloud = new DiscordExplorer("/");
        cloud.showFiles("/home");
        frame.add((Component) cloud, BorderLayout.EAST);

        // default window settings
        frame.setDefaultCloseOperation(EXIT_ON_CLOSE);
        //frame.setSize(,900);
        frame.pack();
    }

    public void show() {
        frame.setVisible(true);
    }
}
