package net.maschmalow.commands.misc;

import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.maschmalow.Utilities;
import net.maschmalow.commands.Command;


public class JoinCommand implements Command {



    @Override
    public void action(String[] args, GuildMessageReceivedEvent e) {
        if (args.length != 0)
            Utilities.sendMessage(e.getChannel(), "Warning: this commands takes no argument, the provided ones are ignored.");

        if (e.getGuild().getAudioManager().getConnectedChannel() != null &&
                e.getGuild().getAudioManager().getConnectedChannel().getMembers().contains(e.getMember()))
            throw new IllegalArgumentException("I am already in your channel!");


        VoiceChannel memberChannel = e.getMember().getVoiceState().getChannel();
        if (memberChannel == null)
            throw new IllegalArgumentException("You need to be in a voice channel to use this command!");

        //write out previous channel's audio if autoSave is on
        //if (e.getGuild().getAudioManager().isConnected() && ServerSettings.get(e.getGuild()).autoSave)
        //    RecorderBot.writeToFile(e.getGuild());

        Utilities.joinVoiceChannel(memberChannel);
    }

    @Override
    public String usage(String prefix) {
        return prefix + "join";
    }

    @Override
    public String description() {
        return "Force the bot to join and record your current channel";
    }


}
