package net.maschmalow.commands.audio;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.maschmalow.Utilities;
import net.maschmalow.commands.Command;


public class ClipCommand implements Command {



    @Override
    public void action(String[] args, GuildMessageReceivedEvent e) {
        if (args.length != 1 && args.length != 2)
            throw new IllegalArgumentException("This command require either one or two arguments");

        if (e.getGuild().getAudioManager().getConnectedChannel() == null) {
            Utilities.sendMessage(e.getChannel(), "I wasn't recording!");
            return;
        }

        TextChannel destChannel = Utilities.findTextChannel(args.length == 2? args[1] : null, e);

        int time = Utilities.parseUInt(args[0]);

        Utilities.saveToFile(e.getGuild(), e.getAuthor().getName(), time);


    }

    @Override
    public String usage(String prefix) {
        return prefix + "clip [seconds] | clip [seconds] [text channel output]";
    }

    @Override
    public String description() {
        return "Saves a clip of the specified length and outputs it in the current or specified text channel";
    }


}
