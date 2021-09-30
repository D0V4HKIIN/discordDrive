package discordDrive;


public class main {
    static private String path = "/home/jonas";

    public static void main(String[] args) {
        Viewer view = new Viewer(path);
        view.show();
    }
}