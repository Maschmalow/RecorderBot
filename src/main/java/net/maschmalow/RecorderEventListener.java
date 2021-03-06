package net.maschmalow;

import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.guild.GuildJoinEvent;
import net.dv8tion.jda.api.events.guild.GuildLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GenericGuildVoiceEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.maschmalow.commands.CommandHandler;
import net.maschmalow.configuration.GuildSettings;
import net.maschmalow.configuration.ServerSettings;

import java.util.List;


public class RecorderEventListener extends ListenerAdapter {

    @Override
    public void onGuildJoin(GuildJoinEvent e) {
        ServerSettings.updateGuilds();
        System.out.format("Joined new server '%s', connected to %s guilds\n", e.getGuild().getName(), e.getJDA().getGuilds().size());
    }

    @Override
    public void onGuildLeave(GuildLeaveEvent e) {
        ServerSettings.updateGuilds();
        System.out.format("Left server '%s', connected to %s guilds\n", e.getGuild().getName(), e.getJDA().getGuilds().size());
    }


    /*
        Whenever somebody joins/leaves, check whether we should change/join voice channels
     */
    @Override
    public void onGenericGuildVoice(GenericGuildVoiceEvent e) {
        if (e.getMember() == null || e.getMember().getUser() == null || e.getMember().getUser().isBot())
            return;
        GuildSettings settings = ServerSettings.get(e.getGuild());
        VoiceChannel currentChannel = e.getGuild().getAudioManager().getConnectedChannel();

        long largestSize = 0;
        VoiceChannel toJoin = null;
        List<VoiceChannel> vcs = e.getGuild().getVoiceChannels();
        for(VoiceChannel v : vcs) {
            long curChannelSize = voiceChannelSize(v);
            if(curChannelSize > largestSize && curChannelSize >= settings.autoJoinSettings) {
                toJoin = v;
                largestSize = curChannelSize;
            }
        }

        if(toJoin != null && toJoin != currentChannel && toJoin != e.getGuild().getAfkChannel()) {
            Utilities.joinVoiceChannel(toJoin);
        } else if(currentChannel != null && voiceChannelSize(currentChannel) <= settings.autoLeaveSettings) {
            Utilities.leaveVoiceChannel(e.getGuild());
        }

    }

    //returns the effective size of the voice channel (bots don't count)
    private static long voiceChannelSize(VoiceChannel vc) {
        if(vc == null) return 0;

        return vc.getMembers().stream()
                .filter((member -> !member.getUser().isBot()))
                .count();
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {
        if (event.getMember() == null || event.getMember().getUser() == null || event.getMember().getUser().isBot())
            return;

        String prefix = ServerSettings.get(event.getGuild()).prefix;

        if (event.getMessage().getContentRaw().startsWith(prefix)) {
            CommandHandler.handleCommand(event);
        }
    }


    @Override
    public void onReady(ReadyEvent e) {
        ServerSettings.updateGuilds(); //in case we were kicked while offline

        RecorderBot.jda.getPresence().setPresence(OnlineStatus.ONLINE, Activity.playing(ServerSettings.getGamePlaying()));
        System.out.format("ONLINE: Connected to %s guilds!\n", e.getJDA().getGuilds().size());

    }
}
