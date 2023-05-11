package net.maschmalow.commands.misc;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.maschmalow.RecorderBot;
import net.maschmalow.commands.SlashCommandBase;
import net.maschmalow.recorderlib.AudioLib;


public class JoinCommand extends SlashCommandBase {


    @Override
    public CommandData getCommandData() {
        return Commands.slash("join","Force the bot to join and record your current channel.")
                .setGuildOnly(true);
    }

    @Override
    public void onInteraction(SlashCommandInteractionEvent e) {
        if (e.getGuild().getAudioManager().getConnectedChannel() != null &&
                e.getGuild().getAudioManager().getConnectedChannel().getMembers().contains(e.getMember())) {
            e.reply("I am already in your channel!").queue();
            return;
        }

        AudioChannelUnion memberChannel = e.getMember().getVoiceState().getChannel();
        if (memberChannel == null){
            e.reply("You need to be in a voice channel to use this command!").queue();
            return;
        }

        if (memberChannel.getId().equals(e.getGuild().getAfkChannel().getId())) {
            e.reply("I won't join afk channels !").queue();
            return;
        }

        e.reply("Joining...").queue((msg) -> msg.deleteOriginal().queue());
        RecorderBot.LOG.info("Joining "+memberChannel.getName()+" voice channel in "+e.getGuild().getName() );


        joinAudio(e.getGuild(), memberChannel);

    }

    public static void joinAudio(Guild guild, AudioChannel channel) {
        RecorderBot.guildsAudio.put(guild, new AudioLib(guild));
        guild.getAudioManager().openAudioConnection(channel);
    }
}
