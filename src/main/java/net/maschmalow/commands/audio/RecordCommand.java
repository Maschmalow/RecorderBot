package net.maschmalow.commands.audio;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.maschmalow.commands.SlashCommandBase;
import net.maschmalow.recorderlib.AudioLib;

import javax.swing.*;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;


public class RecordCommand extends SlashCommandBase {


    @Override
    public CommandData getCommandData() {
        return Commands.slash("record","Records for a specified amount of time ")
                .setGuildOnly(true)
                .addOptions(new OptionData(OptionType.INTEGER, "seconds", "The duration to record.")
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
        int seconds = Math.toIntExact(e.getOption("seconds").getAsLong());

        e.reply("Recording for "+seconds+"s...").queue(hook -> asNonRepeat(new Timer(seconds * 1000, evt -> {
            try {
                ClipCommand.saveToFile(e.getGuild(),
                        hook,
                        e.getChannel().asTextChannel(),
                        (DateFormat.getDateInstance(DateFormat.SHORT).format(new Date()) + e.getMember().getUser().getName() + ".mp3").replaceAll("[^\\w\\-. ]", "_"),
                        seconds);
            } catch (IOException ex) {
                hook.editOriginal("Error saving file: " + ex.getMessage()).queue();
            }
        })).start());

    }

    public static Timer asNonRepeat(Timer timer) {
        timer.setRepeats(false);
        return timer;
    }
}
