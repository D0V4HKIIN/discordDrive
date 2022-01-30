package discordDrive;

import javax.security.auth.login.LoginException;

import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class DiscordInterface {

	public DiscordInterface() {
		// GET ID

		// BUILD BOT
		JDABuilder builder = JDABuilder.createDefault(id);

		// Disable parts of the cache
		builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
		// Enable the bulk delete event
		builder.setBulkDeleteSplittingEnabled(false);
		// Disable compression (not recommended)
		builder.setCompression(Compression.NONE);
		// Set activity (like "playing Something")
		builder.setActivity(Activity.watching("the files"));

		try {
			builder.build();
		} catch (LoginException e) {
			e.printStackTrace();
		}
	}

	public final void main(String[] args) {
		DiscordInterface interface = new DiscordInterface();
	}
}
