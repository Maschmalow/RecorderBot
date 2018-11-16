package net.maschmalow;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.maschmalow.recorderlib.AudioLib;
import net.maschmalow.recorderlib.RecorderAudioHandler;
import net.maschmalow.configuration.ServerSettings;

public class Utilities {
    
    static public TextChannel findTextChannel(String tc, GuildMessageReceivedEvent event) throws IllegalArgumentException {
        if(tc == null)
            return event.getChannel();

        if ( tc.startsWith("#"))
            tc = tc.substring(1);
        try{
            return event.getGuild().getTextChannelsByName(tc, true).get(0);
        } catch(IndexOutOfBoundsException exception) {
            throw new IllegalArgumentException("Cannot find specified text channel");
        }
    }

    static public int parseUInt(String tc)throws IllegalArgumentException {
        int num;
        try {
            num = Integer.parseInt(tc);
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid number entered", ex);
        }

        if (num <= 0) throw new IllegalArgumentException("Number must be strictly greater than 0!");

        return num;
    }


    //general purpose function that sends a message to the given text channel and handles errors
    public static void sendMessage(TextChannel tc, String message) {
        tc.sendMessage("\u200B" + message).queue(null,
                (Throwable) -> tc.getGuild().getDefaultChannel().sendMessage("\u200BI don't have permissions to send messages in " + tc.getName() + "!").queue());
    }

    //general purpose function for leaving voice channels
    public static void leaveVoiceChannel(Guild guild) {
        System.out.format("Leaving voice channel in %s\n", guild.getName());

        if(ServerSettings.get(guild).autoSave)
            AudioLib.writeToFile(guild, "autosave", null, null);  //write data from voice channel it is leaving

        guild.getAudioManager().closeAudioConnection();
        AudioLib.killAudioHandlers(guild);
    }

    public static void joinVoiceChannel(VoiceChannel vc) {
        joinVoiceChannel(vc, false);
    }

    //general purpose function for joining voice channels while warning and handling errors
    public static void joinVoiceChannel(VoiceChannel vc, boolean warning) {
        System.out.format("Joining '%s' voice channel in %s\n", vc.getName(), vc.getGuild().getName());

        //don't join afk channels
        if(vc == vc.getGuild().getAfkChannel()) {
            if(warning) {
                TextChannel tc = vc.getGuild().getDefaultChannel();
                sendMessage(tc, "I don't join afk channels!");
            }
        }

        //attempt to join channel and warn if permission is not available
        try {
            vc.getGuild().getAudioManager().openAudioConnection(vc);
        } catch(Exception e) {
            if(warning) {
                TextChannel tc = vc.getGuild().getDefaultChannel();
                sendMessage(tc, "I don't have permission to join " + vc.getName() + "!");
                return;
            }
        }

        //initalize the audio reciever listener
        double volume = ServerSettings.get(vc.getGuild()).volume;
        vc.getGuild().getAudioManager().setReceivingHandler(new RecorderAudioHandler(volume, vc));

    }
}
