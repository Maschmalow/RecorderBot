package net.maschmalow;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.maschmalow.configuration.ServerSettings;

import javax.security.auth.login.LoginException;

public class RecorderBot {
    //contains the id of every guild that we are connected to and their corresponding GuildSettings object
    public static ServerSettings settings;
    public static JDA jda;


    public static void main(String[] args) {
        new RecorderBot();
    }

    public RecorderBot() {

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
