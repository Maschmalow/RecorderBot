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
        ServerSettings.updateGuilds(); //in case we were kicked while offline


        try {
            RecorderBot.jda = new JDABuilder(AccountType.BOT)
                    .setToken(ServerSettings.getBotToken())
                    .addEventListener(new RecorderEventListener())
                    .buildBlocking();
        } catch(LoginException | InterruptedException e) {
            throw new RuntimeException("Could not login", e);
        }

    }


}
