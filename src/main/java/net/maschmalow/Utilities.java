package net.maschmalow;

import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.VoiceChannel;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.maschmalow.configuration.ServerSettings;
import net.maschmalow.recorderlib.AudioLib;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.Date;

public class Utilities {

    static public TextChannel findTextChannel(String tc, GuildMessageReceivedEvent event) throws IllegalArgumentException {
        if(tc == null)
            return event.getChannel();

        if(tc.startsWith("#"))
            tc = tc.substring(1);
        try {
            return event.getGuild().getTextChannelsByName(tc, true).get(0);
        } catch(IndexOutOfBoundsException exception) {
            throw new IllegalArgumentException("Cannot find specified text channel");
        }
    }

    static public int parseUInt(String tc) throws IllegalArgumentException {
        int num;
        try {
            num = Integer.parseInt(tc);
        } catch(NumberFormatException ex) {
            throw new IllegalArgumentException("Invalid number entered", ex);
        }

        if(num <= 0) throw new IllegalArgumentException("Number must be strictly greater than 0!");

        return num;
    }


    //general purpose function that sends a message to the given text channel and handles errors
    public static void sendMessage(TextChannel tc, String message) {
        tc.sendMessage("\u200B" + message).queue(null,
                (Throwable) -> Utilities.getTextChannel(tc.getGuild()).sendMessage("\u200BI don't have permissions to send messages in " + tc.getName() + "!").queue());
    }

    //general purpose function for leaving voice channels
    public static void leaveVoiceChannel(Guild guild) {
        System.out.format("Leaving voice channel in %s\n", guild.getName());

        //if(ServerSettings.get(guild).autoSave)
        //    AudioLib. (guild, "autosave", null, null);  //write data from voice channel it is leaving

        guild.getAudioManager().closeAudioConnection();

        RecorderBot.guildsAudio.remove(guild).detachFromGuild();
    }


    //general purpose function for joining voice channels while warning and handling errors
    public static void joinVoiceChannel(VoiceChannel vc) {
        System.out.format("Joining '%s' voice channel in %s\n", vc.getName(), vc.getGuild().getName());

        //don't join afk channels

        TextChannel tc = Utilities.getTextChannel(vc.getGuild());
        if(vc == vc.getGuild().getAfkChannel()) {
            sendMessage(tc, "I don't join afk channels!");
        }

        //attempt to join channel and warn if permission is not available
        try {
            vc.getGuild().getAudioManager().openAudioConnection(vc);
        } catch(Exception e) {
            sendMessage(tc, "I don't have permission to join " + vc.getName() + "!");
            return;
        }

        //initalize the audio receiver listener
        RecorderBot.guildsAudio.put(vc.getGuild(), new AudioLib(vc.getGuild()));

    }

    public static void saveToFile(Guild guild, String filename, Integer time) {
        TextChannel tc = Utilities.getTextChannel(guild);

        if(filename == null)
            filename = "untitled_recording";

        filename = DateFormat.getDateInstance(DateFormat.SHORT).format(new Date()) + filename + ".mp3";
        filename = filename.replaceAll("[^\\w\\-. ]", "_");//sanitize filename

        File dest = Paths.get(ServerSettings.getRecordingsPath(), filename).toFile();


        try {
            RecorderBot.guildsAudio.get(guild).flushToOStream(new FileOutputStream(dest), time);
        } catch(IOException ex) {
            Utilities.sendMessage(tc, "Error saving file: " + ex.getMessage());
            return;
        }

        System.out.format("Saved audio file '%s' from %s on %s of size %f MB\n",
                dest.getName(), guild.getAudioManager().getConnectedChannel().getName(), guild.getName(), (double) dest.length() / 1024 / 1024);


        String waitMessage = "";
        if(dest.length() < RecorderBot.jda.getSelfUser().getAllowedFileSize()) {
            waitMessage = "\nIt will also be uploaded to this text channel.";
            tc.sendFile(dest).queue(null, (Throwable) -> Utilities.sendMessage(tc,
                    "I don't have permissions to send files in " + tc.getName() + "!"));
        }

        try {
            sendMessage(tc, "New audio clip is available at " + ServerSettings.getRecordingsURL() + URLEncoder.encode(dest.getName(),"UTF-8") + "." + waitMessage);
        } catch(UnsupportedEncodingException e) {
            return;
        }
    }


    public static TextChannel getTextChannel(Guild guild) {
        return guild.getSystemChannel(); //to be improved
    }
}
