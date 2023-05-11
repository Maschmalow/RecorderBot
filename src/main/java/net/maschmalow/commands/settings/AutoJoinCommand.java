package net.maschmalow.commands.settings;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.maschmalow.commands.SlashCommandBase;
import net.maschmalow.configuration.ServerSettings;


public class AutoJoinCommand  extends SlashCommandBase {

    @Override
    public CommandData getCommandData() {
        return Commands.slash("autojoin","Sets the number of users for the bot to auto-join a voice channel. Set to 0 to disable entirely.")
                .setGuildOnly(true)
                .addOptions(new OptionData(OptionType.INTEGER, "user-count", "The number of users to auto-join.")
                        .setRequired(true)
                        .setMinValue(0));
    }

    @Override
    public void onInteraction(SlashCommandInteractionEvent e) {
        int num = Math.toIntExact(e.getOption("user-count").getAsLong());
        if(num == 0)
            num = Integer.MAX_VALUE;

        ServerSettings.get(e.getGuild()).autoJoinSettings = num;
        ServerSettings.write();

        if(num != Integer.MAX_VALUE)
            e.reply("Will now automatically join any voice channel with " + num + " people").queue();
        else
            e.reply("Will no longer automatically join any channel").queue();
    }



}
