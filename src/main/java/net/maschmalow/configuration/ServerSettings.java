package net.maschmalow.configuration;

import com.google.gson.Gson;
import net.dv8tion.jda.api.entities.Guild;
import net.maschmalow.RecorderBot;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ServerSettings {

    private static ServerSettings instance = new ServerSettings();
    private static final String SETTINGS_PATH = "settings.json";

    /*
     * DO NOT set the settings fields as final, unless you want to set you up for a terrible debugging session.
     * Gson will populate them using field reflection.
     * Field.set() documentation :
     * "Setting a final field in this way is meaningful only during deserialization or reconstruction of instances of
     *  classes with blank final fields, before they are made available for access by other parts of a program. Use
     * in any other context may have unpredictable effects, including cases in which other parts of a program continue
     * to use the original value of this field."
     * This behavior appeared suddenly, despite having exactly the same code running fine for years and still working
     * in other copy of this program. Note that "other parts of a program" also include the debugger.
     */
    private String recordingsPath = "C:\\path\\to\\your\\recording\\folder\\";
    private String recordingsURL = "https://example.net/Discord/recordings";
    private String gamePlaying = "!help for help";
    private Map<String, GuildSettings> guildsSettings = new HashMap<>();
    private String botToken = "YOUR_BOT_TOKEN_HERE";


    public static GuildSettings get(Guild g) {
        return instance.guildsSettings.get(g.getId());
    }

    public static Set<String> guilds() {
        return instance.guildsSettings.keySet();
    }

    /**
     * Synchronise guilds in settings files with guild list from jda
     */
    public static void updateGuilds() {
        boolean update = false; // :(
        for(Guild g : RecorderBot.jda.getGuilds()) {
            if(!instance.guildsSettings.containsKey(g.getId())) {
                instance.guildsSettings.put(g.getId(), new GuildSettings(g));
                update = true;
            }
        }

        for(String guildId : instance.guildsSettings.keySet()) {
            if(RecorderBot.jda.getGuildById(guildId) == null) {
                instance.guildsSettings.remove(guildId);
                update = true;
            }
        }

        if(update)
            ServerSettings.write();
    }


    public static void read() {
        if(Files.notExists(Paths.get(SETTINGS_PATH))) {
            write();
            throw new RuntimeException("Config file did not exist at \"" + Paths.get(SETTINGS_PATH).toAbsolutePath() + "\"\n." +
                    " A default one has been created, but the bot token should at least be populated.");
        }

        try {
            FileReader fileReader = new FileReader(SETTINGS_PATH);
            instance = new Gson().fromJson(fileReader, ServerSettings.class);
            fileReader.close();
            if(instance == null)
                throw new RuntimeException("Invalid configuration file");
        } catch(IOException e) {
            throw new RuntimeException("Could not read configuration file at " + SETTINGS_PATH, e);
        }

        if(Files.notExists(Paths.get(instance.recordingsPath))) {
            try { //try to create recording directory if not existing
                Files.createDirectories(Paths.get(instance.recordingsPath));
            } catch(IOException ignored) {
            }
        }
    }


    //write the current state of all server settings to the settings.json file
    public static void write() {
        try {
            FileWriter fw = new FileWriter(SETTINGS_PATH);
            new Gson().toJson(instance, fw);
            fw.close();
        } catch(IOException e) {
            throw new RuntimeException("Could not write configuration file at " + SETTINGS_PATH, e);
        }
    }

    public static String getRecordingsPath() {
        return instance.recordingsPath;
    }

    public static String getBotToken() {
        return instance.botToken;
    }

    public static String getRecordingsURL() {
        return instance.recordingsURL;
    }

    public static String getGamePlaying() {
        return instance.gamePlaying;
    }
}
