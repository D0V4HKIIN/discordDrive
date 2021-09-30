# discordDrive
Saves files in discord messages using a bot


# how to setup
create a discord server and create 2 text channels.
copy the discord server's id and paste it in config.json in "control_server".
copy the channel's id that you want to use for logging and paste it in "logs_channel_id".
and finally do the same for "file_table_id" with the 2nd channel.

Add your bots token to the enironment variable "DISCORD"

now start the bot with ```node main.js```

As for now for uploading and downloading files you will have to mess with the code as it is still experimental.

# future of the project
I am currently working on creating an java app from this proof of concept.
Maybe later also will do an android app.
Also i plan on making use of attachement files to make the upload faster.
