package net.maschmalow.commands.misc;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.maschmalow.Utilities;
import net.maschmalow.commands.Command;


public class LeaveCommand implements Command {


    @Override
    public void action(String[] args, GuildMessageReceivedEvent e) {
        if(args.length != 0)
            Utilities.sendMessage(e.getChannel(), "Warning: this commands takes no argument, the provided ones are ignored.");

        if(!e.getGuild().getAudioManager().isConnected())
            throw new IllegalArgumentException("I am not in a channel!");

        Utilities.leaveVoiceChannel(e.getGuild());

    }

    @Override
    public String usage(String prefix) {
        return prefix + "leave";
    }

    @Override
    public String description() {
        return "Force the bot to leave its current channel.";
    }


}
