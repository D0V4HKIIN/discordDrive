package discordDrive;

import javax.security.auth.login.LoginException;

import discordInterface.DiscordInterface;

public class Launcher {
	static private String path = System.getenv("HOME");

	public static void main(String[] args) throws LoginException, InterruptedException {
		DiscordInterface dInterface = new DiscordInterface();
		// debug
		dInterface.resetServer();
		Viewer view = new Viewer(path, dInterface);
		view.show();
	}
}