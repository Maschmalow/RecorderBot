package net.maschmalow.commands.audio;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.InteractionHook;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.utils.FileUpload;
import net.maschmalow.RecorderBot;
import net.maschmalow.commands.SlashCommandBase;
import net.maschmalow.configuration.ServerSettings;
import net.maschmalow.recorderlib.AudioLib;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.util.Date;


public class ClipCommand extends SlashCommandBase {


    @Override
    public CommandData getCommandData() {
        return Commands.slash("clip", "Saves a clip of the specified length.")
                .setGuildOnly(true)
                .addOptions(new OptionData(OptionType.INTEGER, "seconds", "The length of the clip in seconds.")
                        .setRequired(true)
                        .setMinValue(1)
                        .setMaxValue(AudioLib.AUDIOBUF_MAXSIZE * 1024 * 1024 / AudioLib.BYTES_PER_SEC - 1));
    }

    @Override
    public void onInteraction(SlashCommandInteractionEvent e) {
        if (e.getGuild().getAudioManager().getConnectedChannel() == null) {
            e.reply("I wasn't recording!").queue();
            return;
        }

        InteractionHook hook = e.deferReply().submit().join();

        try {
            saveToFile(e.getGuild(),
                    hook,
                    e.getChannel().asTextChannel(),
                    (DateFormat.getDateInstance(DateFormat.SHORT).format(new Date()) + e.getMember().getUser().getName() + ".mp3").replaceAll("[^\\w\\-. ]", "_"),
                    Math.toIntExact(e.getOption("seconds").getAsLong()));
        } catch (IOException ex) {
            hook.editOriginal("Error saving file: " + ex.getMessage()).queue();
        }


    }

    public static void saveToFile(Guild guild, InteractionHook hook, TextChannel tc, String filename, Integer time) throws IOException {

        if (filename == null)
            filename = "untitled_recording";

        File dest = Paths.get(ServerSettings.getRecordingsPath(), filename).toFile();

        RecorderBot.guildsAudio.get(guild).flushToOStream(Files.newOutputStream(dest.toPath()), time);

        RecorderBot.LOG.info("Saved audio file '{}' from {} on {} of size {} MB\n",
                dest.getName(), guild.getAudioManager().getConnectedChannel().getName(), guild.getName(), (double) dest.length() / 1024 / 1024);

        String waitMessage = "";
        if (dest.length() < RecorderBot.jda.getSelfUser().getAllowedFileSize()) {
            waitMessage = "\nIt will also be uploaded to this text channel.";
            tc.sendFiles(FileUpload.fromData(dest)).queue(null, (Throwable) ->
                    hook.editOriginal("I don't have permissions to send files in " + tc.getName() + "!").queue());
        }

        hook.editOriginal("New audio clip is available at " + ServerSettings.getRecordingsURL() + URLEncoder.encode(dest.getName(), "UTF-8") + waitMessage).queue();

    }


}
