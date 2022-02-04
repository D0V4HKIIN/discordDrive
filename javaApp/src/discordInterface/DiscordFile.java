package discordInterface;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class DiscordFile {
	String path;
	Long channel;
	Long entry;

	public DiscordFile(String path, Long channel, Long entry) {
		this.path = path;
		this.channel = channel;
		this.entry = entry;
	}

	public DiscordFile(String jsonString) {
		try {
			JSONParser parser = new JSONParser();
			JSONObject jsonObject = (JSONObject) parser.parse(jsonString);
			this.path = (String) jsonObject.get("path");
			this.channel = (Long) jsonObject.get("channel");
			this.entry = (Long) jsonObject.get("entry");
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String toString() {
		return "{\"path\":\"" + path + "\", \"channel\":" + channel + ", \"entry\":" + entry + "}";
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public Long getChannel() {
		return channel;
	}

	public void setChannel(Long channel) {
		this.channel = channel;
	}

	public Long getEntry() {
		return entry;
	}

	public void setEntry(Long entry) {
		this.entry = entry;
	}
}
