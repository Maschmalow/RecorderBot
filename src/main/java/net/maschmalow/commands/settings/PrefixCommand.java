package net.maschmalow.commands.settings;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.maschmalow.Utilities;
import net.maschmalow.commands.Command;
import net.maschmalow.configuration.ServerSettings;


public class PrefixCommand implements Command {

    @Override
    public void action(String[] args, GuildMessageReceivedEvent e) throws IllegalArgumentException {
        if (args.length != 1)
            throw new IllegalArgumentException("This command require exactly one argument");

        ServerSettings.get(e.getGuild()).prefix = args[0];
        ServerSettings.write();

        Utilities.sendMessage(e.getChannel(), "Command prefix now set to " + args[0]);
    }

    @Override
    public String usage(String prefix) {
        return prefix + "prefix [string]";
    }

    @Override
    public String description() {
        return "Sets the prefix for each command to avoid conflict with other bots (Default is '!')";
    }

}
