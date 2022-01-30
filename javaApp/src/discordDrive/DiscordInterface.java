package discordDrive;

import java.io.File;
import java.io.FileReader;
import java.util.List;

import javax.security.auth.login.LoginException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class DiscordInterface {
	JDA jda;
	Guild guild;
	TextChannel table;
	TextChannel logs;

	public DiscordInterface() throws InterruptedException, LoginException {
		// parse config.json
		JSONParser parser = new JSONParser();
		String id = null;

		Long guildId = null;
		Long tableId = null;
		Long logsId = null;

		try {
			String basePath = new File("").getAbsolutePath();

			JSONObject config = (JSONObject) parser.parse(new FileReader(basePath + File.separator + "config.json"));
			id = (String) config.get("bot_id");
			guildId = (Long) config.get("control_server");
			tableId = (Long) config.get("file_table_id");
			logsId = (Long) config.get("logs_channel_id");

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("There is a problem with config.json");
		}

		// build bot
		JDABuilder builder = JDABuilder.createDefault(id);

		// Disable parts of the cache
		builder.disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE);
		// Enable the bulk delete event
		builder.setBulkDeleteSplittingEnabled(false);
		// Disable compression (not recommended)
		builder.setCompression(Compression.NONE);
		// Set activity (like "playing Something")
		builder.setActivity(Activity.watching("the files"));

		jda = builder.build();
		jda.awaitReady();

		// save guild and channels
		guild = jda.getGuildById(guildId);
		table = (TextChannel) guild.getGuildChannelById(tableId);
		logs = (TextChannel) guild.getGuildChannelById(logsId);
	}

	public void getFiles() {
		List<Message> msgs = table.getHistory().retrievePast(100).complete();

		for (Message msg : msgs) {
			System.out.println(msg.getContentDisplay());
		}
	}

	public static void main(String[] args) {
		DiscordInterface dInterface = null;
		try {
			dInterface = new DiscordInterface();
		} catch (LoginException | InterruptedException e) {
			e.printStackTrace();
		}
		dInterface.getFiles();
	}
}
