const Discord = require("discord.js");
const { Client, Intents } = Discord;
const token = process.env.DISCORD;
const { control_server, logs_channel_id, file_table_id } = require("./config.json");
const client = new Client({ intents: [Intents.FLAGS.GUILD_MEMBERS] });

const fs = require("fs");

var discord = {};

client.on("ready", async () => {
	console.log(`Logged in as ${client.user.tag}!`);

	discord.main_guild = client.guilds.cache.get(control_server);

	await Promise.all(
		[fetchChannel(logs_channel_id, "logs_channel"),
		fetchChannel(file_table_id, "file_table")]
	);

	//upload("./image.png");
	//uploadFolder("./");
	downloadAll();
});

function uploadFolder(folder_path) {
	let files = fs.readdirSync(folder_path);
	files.forEach(file => {
		upload(file);
	});
}

async function upload(file_path) {
	fs.readFile(file_path, null, async function (err, data) {
		if (err) {
			console.log(err);
			log(file_path + " failed to read file");
		} else {
			// create channel
			log("creating channel " + file_path);
			let file_channel;
			await discord.main_guild.channels.create(file_path)
				.then(channel => file_channel = channel);

			// send first message
			let message = await file_channel.send("START");

			// create file object
			let file = {
				path: file_path,
				channel: file_channel.id,
				entry: message.id
			}

			// send to file table
			discord.file_table.send(JSON.stringify(file));

			// upload file to channel
			log("attempting to upload file " + file_path);

			parts = data.toString("base64").match(/[\s\S]{1,2000}/g) || [];

			// send the rest of the file
			for (let i = 0; i < parts.length; i++) {
				// this probably slows down the upload
				//log(["uploading... ", i + 1, "/", parts.length].join(""));
				file_channel.send(parts[i]);
			}
			log(file_path + " uploaded successfully");
		}
	});
}

function downloadAll() {
	// get all files from the file table
	log("downloading all");
	discord.file_table.messages.fetch()
		.then(messages => messages.forEach(message => {
			// download file
			log("fetched files");
			let file = JSON.parse(message.content);

			// get channel
			client.channels.fetch(file.channel)
				.then(async channel => {
					log("downloading " + file.path);

					if (fs.existsSync(file.path)) {
						let folder = "./backup" + Math.floor(Date.now()/10000);
						fs.mkdir(folder, err => {
							fs.renameSync(file.path, folder + "/" + file.path);
							downloadAfter(file.entry, channel, file);
						});
					} else {
						downloadAfter(file.entry, channel, file);
					}
				});
		}));
}

function downloadAfter(message_id, channel, file) {

	// get file content and create local file
	channel.messages.fetch({ limit: 100, after: message_id })
		.then(messages => {
			let last_message;
			messages.sort(
				function (a, b) {
					return a.createdTimestamp - b.createdTimestamp;
				}
			).forEach(message => {
				fs.appendFileSync(file.path, Buffer.from(message.content, "base64"));
				last_message = message.id;
			});
			// check if it is at the end of the file
			if (messages.size != 100) {
				log("finished downloading " + file.path);
			} else {
				downloadAfter(last_message, channel, file);
			}
		}).catch(err => console.log(err));
}

function log(text) {
	console.log("logging " + text);
	discord.logs_channel.send(text);
}

async function fetchChannel(id, name) {
	await client.channels.fetch(id).then(channel => {
		discord[name] = channel;
	});
}

client.login(token);