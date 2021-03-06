package net.maschmalow.commands.settings;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.maschmalow.Utilities;
import net.maschmalow.commands.Command;
import net.maschmalow.configuration.ServerSettings;


public class VolumeCommand implements Command {

    @Override
    public void action(String[] args, GuildMessageReceivedEvent e) throws IllegalArgumentException {
        if(args.length != 1)
            throw new IllegalArgumentException("This command requires exactly one argument");

        int num = Utilities.parseUInt(args[0]);

        if(num < 1 || num > 100)
            throw new IllegalArgumentException("Volume must be between 1 and 100");


        ServerSettings.get(e.getGuild()).volume = (double) num / 100.0;
        ServerSettings.write();

        Utilities.sendMessage(e.getChannel(), "Volume set to " + num + "% for next recordings!");


    }

    @Override
    public String usage(String prefix) {
        return prefix + "volume [1-100]";
    }

    @Override
    public String description() {
        return "Sets the percentage volume to record at, from 1-100%";
    }

}
