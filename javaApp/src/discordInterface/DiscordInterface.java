package discordInterface;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import javax.security.auth.login.LoginException;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.Channel;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.RestAction;
import net.dv8tion.jda.api.utils.Compression;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class DiscordInterface {
	JDA jda;
	JSONParser parser;
	Guild guild;
	TextChannel table;
	TextChannel logs;
	static final int MAX_CHARS_IN_MESSAGE = 2000;
	static final int MAX_HISTORY = 100;

	public DiscordInterface() throws InterruptedException, LoginException {
		// parse config.json
		parser = new JSONParser();
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
		// Set activity
		builder.setActivity(Activity.watching("the files"));

		jda = builder.build();
		jda.awaitReady();

		// save guild and channels
		guild = jda.getGuildById(guildId);
		table = guild.getTextChannelById(tableId);
		logs = guild.getTextChannelById(logsId);
	}

	private void log(String message) {
		System.out.println(message);
		logs.sendMessage(message).submit();
	}

	private List<Message> getAllMessagesInChannel(TextChannel channel) {

		List<Message> msgs = new ArrayList<Message>();
		msgs.addAll(channel.getHistory().retrievePast(MAX_HISTORY).complete());

		return msgs;
	}

	public CompletableFuture<Message> upload(String path, String destination) {
		// add filename to destination
		String regex = "\\" + File.separator + "";
		String[] folders = path.split(regex);
		destination = destination + folders[folders.length - 1];

		// create channel
		log("creating channel " + path);
		TextChannel channel = guild.createTextChannel(destination).complete();

		CompletableFuture<Message> lastMessage = null;

		// send start message
		CompletableFuture<Message> startFuture = channel.sendMessage("START").submit();
		try {
			// read file
			byte[] data = Files.readAllBytes(Paths.get(path));

			String[] encodedString = Base64.getEncoder().encodeToString(data)
					.split("(?<=\\G.{" + MAX_CHARS_IN_MESSAGE + "})");

			// track upload
			int i = 0;

			for (String part : encodedString) {
				// track upload
				log("uploading... " + i + "/" + (encodedString.length - 1));
				i++;

				// send
				lastMessage = channel.sendMessage(part).submit();
			}

			// update table
			table.sendMessage(
					new DiscordFile(destination, channel.getIdLong(), startFuture.get().getIdLong()).toString())
					.submit();

			log(path + " uploaded");

		} catch (IOException | InterruptedException | ExecutionException e) {
			e.printStackTrace();
		}

		return lastMessage;
	}

	public void uploadFiles(List<String> pathes, String destination) {
		for (String path : pathes) {
			upload(path, destination);
		}
	}

	// destination does not contain the files name
	public void download(String path, String destination) throws IOException {
		log("starting download of " + path);

		// add filename to destination
		String[] folders = path.split(File.separator);
		destination = destination + folders[folders.length - 1];

		File destinationFile = new File(destination);

		// check if file exists
		// TODO Handle this case
		if (destinationFile.exists()) {
			log("file already exists. This is not yet handled by the bot. File in question: " + path);
			throw new IOException("file already exists. this is not yet implemented");
		}

		DiscordFile file = getFile(path);
		TextChannel chan = guild.getTextChannelById(file.getChannel());
		Message entryMsg = chan.retrieveMessageById(file.getEntry()).complete();

		downloadAfter(chan, entryMsg, new StringBuilder(), new FileOutputStream(destination));
	}

	// destination here also contains the files name
	private void downloadAfter(TextChannel channel, Message lastMessage, StringBuilder builder,
			FileOutputStream stream) throws IOException {
		List<Message> msgs = channel.getHistoryAfter(lastMessage, MAX_HISTORY).complete().getRetrievedHistory();

		for (int i = msgs.size() - 1; i >= 0; i--) {
			builder.append(msgs.get(i).getContentDisplay());
		}

		if (msgs.size() == MAX_HISTORY) {
			log("next recursive call for download");
			downloadAfter(channel, msgs.get(0), builder, stream);
		} else {
			log("decoding and writing file");
			stream.write(Base64.getDecoder().decode(builder.toString()));
			stream.close();
			log("finished downloading");
		}
	}

	// destination does not contain the files name
	public void downloadFiles(List<String> pathes, String destination) {
		for (String path : pathes) {
			try {
				download(path, destination);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	// TODO this is slow and should use some form of tree
	private DiscordFile getFile(String path) {
		List<DiscordFile> files = getFiles();

		for (DiscordFile file : files) {
			if (file.getPath().equals(path)) {
				return file;
			}
		}

		// file not found
		return null;
	}

	// TODO create a tree instead of list
	public List<DiscordFile> getFiles() {
		List<Message> msgs = getAllMessagesInChannel(table);
		List<DiscordFile> files = new ArrayList<DiscordFile>();

		for (Message msg : msgs) {
			files.add(new DiscordFile(msg.getContentDisplay()));
		}

		return files;
	}
	
	// only for debug
	public void resetServer() {
		System.out.println("resetting server");
		List<CompletableFuture<?>> actions = new ArrayList<CompletableFuture<?>>();
		for(Channel channel: guild.getChannels()) {
			if(!channel.equals(logs) && !channel.equals(table)) {
				actions.add(channel.delete().submit());
			}
		}
		for(Message msg: getAllMessagesInChannel(table)) {
			actions.add(msg.delete().submit());
		}
		for(Message msg: getAllMessagesInChannel(logs)) {
			actions.add(msg.delete().submit());
		}
		for(CompletableFuture<?> action: actions) {
			action.join();
		}
		System.out.println("server reset");
	}

	public static void main(String[] args) {
		System.out.println("starting discord interface debug");
		DiscordInterface dInterface = null;
		try {
			dInterface = new DiscordInterface();
		} catch (LoginException | InterruptedException e) {
			e.printStackTrace();
			System.exit(1);
		}

		// upload
		try {
			dInterface.upload("/home/jonas/eclipse-workspace/discordDrive/test_files/test.txt", "/data/").get();
		} catch (InterruptedException | ExecutionException e1) {
			e1.printStackTrace();
		}

		// download
		try {
			dInterface.download("/data//data/.dir_colors", "/home/jonas/eclipse-workspace/discordDrive/test_download/");
		} catch (IOException e) {
			e.printStackTrace();
		}

		dInterface.jda.shutdown();
	}
}
