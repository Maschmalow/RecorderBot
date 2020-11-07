package net.maschmalow;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.maschmalow.configuration.ServerSettings;
import net.maschmalow.recorderlib.AudioLib;

import javax.security.auth.login.LoginException;
import java.util.HashMap;
import java.util.Map;

public class RecorderBot {

    public static JDA jda;
    public static Map<Guild, AudioLib> guildsAudio = new HashMap<>();


    public static void main(String[] args) {
        new RecorderBot();
    }

    private RecorderBot() {

        ServerSettings.read();

        try {
            RecorderBot.jda = JDABuilder.create(ServerSettings.getBotToken(),
                        GatewayIntent.GUILD_VOICE_STATES,
                        GatewayIntent.DIRECT_MESSAGES,
                        GatewayIntent.GUILD_MESSAGES)
                    .addEventListeners(new RecorderEventListener())
                    .build();
        } catch(LoginException e) {
            throw new RuntimeException("Could not login", e);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            RecorderBot.jda.shutdownNow();
            System.out.format("Successfully shutdown.\n");
        }));
    }


}
