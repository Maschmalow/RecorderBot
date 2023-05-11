package net.maschmalow;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.middleman.AudioChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceUpdateEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.maschmalow.commands.misc.JoinCommand;
import net.maschmalow.commands.misc.LeaveCommand;
import net.maschmalow.configuration.GuildSettings;
import net.maschmalow.configuration.ServerSettings;

import java.util.List;


public class RecorderEventListener extends ListenerAdapter {

    @Override
    public void onGuildJoin(GuildJoinEvent e) {
        ServerSettings.updateGuilds();
        RecorderBot.LOG.info("Joined server "+e.getGuild().getName()+", connected to "+e.getJDA().getGuilds().size()+" guilds" );
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent e) {
        ServerSettings.updateGuilds();
        RecorderBot.LOG.info("Left server "+e.getGuild().getName()+", connected to "+e.getJDA().getGuilds().size()+" guilds" );
    }


    /*
        Whenever somebody joins/leaves, check whether we should change/join voice channels
     */
    @Override
    public void onGuildVoiceUpdate(GuildVoiceUpdateEvent e) {
        if (e.getMember() == null || e.getMember().getUser() == null || e.getMember().getUser().isBot())
            return;

        Guild guild = e.getGuild();
        GuildSettings settings = ServerSettings.get(guild);
        AudioChannelUnion currentChannel = guild.getAudioManager().getConnectedChannel();

        long largestSize = 0;
        VoiceChannel toJoin = null;
        List<VoiceChannel> vcs = guild.getVoiceChannels();
        for(VoiceChannel v : vcs) {
            long curChannelSize = voiceChannelSize(v);
            if(curChannelSize > largestSize && curChannelSize >= settings.autoJoinSettings) {
                toJoin = v;
                largestSize = curChannelSize;
            }
        }

        if(toJoin != null && toJoin != currentChannel && toJoin != guild.getAfkChannel()) {
            JoinCommand.joinAudio(guild, toJoin);

        } else if(currentChannel != null && voiceChannelSize(currentChannel) <= settings.autoLeaveSettings) {
            LeaveCommand.leaveAudio(guild);
        }

    }

    //returns the effective size of the voice channel (bots don't count)
    private static long voiceChannelSize(AudioChannel vc) {
        if(vc == null) return 0;

        return vc.getMembers().stream()
                .filter((member -> !member.getUser().isBot()))
                .count();
    }


    @Override
    public void onReady(ReadyEvent e) {
        ServerSettings.updateGuilds(); //in case we were kicked while offline

        RecorderBot.jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.playing(ServerSettings.getGamePlaying()));
        RecorderBot.LOG.info("ONLINE: Connected to {} guilds!\n", e.getJDA().getGuilds().size());

    }
}
