package net.maschmalow.commands.settings;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.maschmalow.RecorderBot;
import net.maschmalow.commands.SlashCommandBase;
import net.maschmalow.configuration.ServerSettings;


public class VolumeCommand extends SlashCommandBase {




    @Override
    public CommandData getCommandData() {
        return Commands.slash("volume","Sets the recording volume.")
                .addOptions(new OptionData(OptionType.INTEGER, "volume", "Sets the percentage volume to record at, from 1-100%.")
                        .setMinValue(1)
                        .setMaxValue(100)
                        .setRequired(true))
                .setGuildOnly(true);
    }

    @Override
    public void onInteraction(SlashCommandInteractionEvent e) {
        e.reply("Volume set to "+e.getOption("volume").getAsLong() ).queue();
        double volume = (double) e.getOption("volume").getAsLong() / 100.0;
        ServerSettings.get(e.getGuild()).volume = volume;
        ServerSettings.write();
        RecorderBot.guildsAudio.get(e.getGuild()).setVolume(volume);

    }
}
