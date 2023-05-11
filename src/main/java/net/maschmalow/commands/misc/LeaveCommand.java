package net.maschmalow.commands.misc;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.maschmalow.RecorderBot;
import net.maschmalow.commands.SlashCommandBase;


public class LeaveCommand extends SlashCommandBase {


    @Override
    public CommandData getCommandData() {
        return Commands.slash("leave","Force the bot to leave its current channel.")
                .setGuildOnly(true);
    }

    @Override
    public void onInteraction(SlashCommandInteractionEvent e) {
        e.reply("Bye").queue((msg) -> msg.deleteOriginal().queue());

        RecorderBot.LOG.info("Leaving voice channel in "+e.getGuild().getName() );

        leaveAudio(e.getGuild());
    }

    public static void leaveAudio(Guild guild) {
        guild.getAudioManager().closeAudioConnection();
        RecorderBot.guildsAudio.remove(guild).detachFromGuild();
    }
}
