package net.maschmalow;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.entities.Guild;
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
            RecorderBot.jda = new JDABuilder(AccountType.BOT)
                    .setToken(ServerSettings.getBotToken())
                    .addEventListener(new RecorderEventListener())
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
