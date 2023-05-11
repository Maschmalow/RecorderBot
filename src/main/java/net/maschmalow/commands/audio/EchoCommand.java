package net.maschmalow.commands.audio;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.maschmalow.RecorderBot;
import net.maschmalow.commands.SlashCommandBase;
import net.maschmalow.recorderlib.AudioLib;

import java.util.concurrent.TimeUnit;


public class EchoCommand extends SlashCommandBase {

    @Override
    public CommandData getCommandData() {
        return Commands.slash("echo","Echos back the input number of seconds of the recording into the voice channel")
                .setGuildOnly(true)
                .addOptions(new OptionData(OptionType.INTEGER, "seconds", "The length of the clip in seconds.")
                        .setRequired(true)
                        .setMinValue(1)
                        .setMaxValue(AudioLib.AUDIOBUF_MAXSIZE*1024*1024/AudioLib.BYTES_PER_SEC-1));
    }

    @Override
    public void onInteraction(SlashCommandInteractionEvent e) {
        if (e.getGuild().getAudioManager().getConnectedChannel() == null) {
            e.reply("I wasn't recording!").queue();
            return;
        }


        e.reply("Echoing...").queue((msg)->msg.deleteOriginal().queueAfter(1, TimeUnit.SECONDS));
        RecorderBot.guildsAudio.get(e.getGuild()).flushToSender(Math.toIntExact(e.getOption("seconds").getAsLong()));
    }
}
