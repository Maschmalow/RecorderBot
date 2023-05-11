package net.maschmalow;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.maschmalow.commands.SlashCommandsManager;
import net.maschmalow.configuration.ServerSettings;
import net.maschmalow.recorderlib.AudioLib;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class RecorderBot {

    public static JDA jda;
    public static Logger LOG = LoggerFactory.getLogger(RecorderBot.class);
    public static Map<Guild, AudioLib> guildsAudio = new HashMap<>();


    public static void main(String[] args) {
        new RecorderBot();
    }

    private RecorderBot() {

        ServerSettings.read();


        RecorderBot.jda = JDABuilder.create(ServerSettings.getBotToken(),
                        GatewayIntent.GUILD_VOICE_STATES,
                        GatewayIntent.DIRECT_MESSAGES,
                        GatewayIntent.GUILD_MEMBERS, // must be enabled in the developer portal
                        GatewayIntent.GUILD_MESSAGES)
                .setMemberCachePolicy(MemberCachePolicy.ALL)
                .addEventListeners(new RecorderEventListener(), SlashCommandsManager.makeCommandsManager())
                .build();

    }


}
