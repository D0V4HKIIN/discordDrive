package discordDrive;

public class Launcher {
	static private String path = "/home/jonas/Documents/code/discordDrive";

	public static void main(String[] args) {
		Viewer view = new Viewer(path);
		view.show();
	}
}