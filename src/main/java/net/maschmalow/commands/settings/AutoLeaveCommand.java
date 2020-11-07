package net.maschmalow.commands.settings;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.maschmalow.Utilities;
import net.maschmalow.commands.Command;
import net.maschmalow.configuration.ServerSettings;


public class AutoLeaveCommand implements Command {



    @Override
    public void action(String[] args, GuildMessageReceivedEvent e) throws IllegalArgumentException{
        if (args.length != 1)
            throw new IllegalArgumentException("This command requires exactly one argument");

        int num = Utilities.parseUInt(args[0]);
        ServerSettings.get(e.getGuild()).autoLeaveSettings = num;
        ServerSettings.write();

        Utilities.sendMessage(e.getChannel(), "Will now automatically leave any voice channel with " + num + " people");
    }

    @Override
    public String usage(String prefix) {
        return prefix + "autoleave [number]";
    }

    @Override
    public String description() {
        return "Sets the number of players for the bot to auto-leave a voice channel.";
    }

}
