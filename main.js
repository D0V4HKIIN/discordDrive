const Discord = require("discord.js");
const { Client, Intents } = Discord;
const token = process.env.DISCORD;
console.log(token);
const client = new Client({ intents: [Intents.FLAGS.GUILD_MEMBERS] });

client.on("ready", () => {
	console.log(`Logged in as ${client.user.tag}!`);
	client.user.setStatus("invisible");
	client.api.applications(client.user.id).guilds(control_server).commands.post({
		data: {
			name: "hello",
			description: "hello world command",
			options: [{
				type: 3,
				name:"test1",
				description: "test1 desc",
				required: false
			}]
		}	
	});
});

client.ws.on("INTERACTION_CREATE", async interaction => {
	const command = interaction.data.name.toLowerCase();
	const args = interaction.data.options;

	if (command === "hello"){ 
		client.api.interactions(interaction.id, interaction.token).callback.post({
			data: {
				type: 4,
				data: {
					content: "hello world!!! " + (args ? args[0].value : "")
				}
			}
		})
	}
});

client.login(token);