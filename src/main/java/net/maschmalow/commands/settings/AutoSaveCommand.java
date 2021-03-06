package net.maschmalow.commands.settings;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.maschmalow.Utilities;
import net.maschmalow.commands.Command;
import net.maschmalow.configuration.ServerSettings;

/*
    Disabled for now
 */
public class AutoSaveCommand implements Command {

    @Override
    public void action(String[] args, GuildMessageReceivedEvent e) {
        if (args.length != 0)
            Utilities.sendMessage(e.getChannel(), "Warning: this commands takes no argument, the provided ones are ignored.");

        ServerSettings.get(e.getGuild()).autoSave = !ServerSettings.get(e.getGuild()).autoSave;

        if (ServerSettings.get(e.getGuild()).autoSave)
            Utilities.sendMessage(e.getChannel(), "No longer saving at the end of each session!");
        else
            Utilities.sendMessage(e.getChannel(), "Now saving at the end of each session!");

    }

    @Override
    public String usage(String prefix) {
        return prefix + "autosave";
    }

    @Override
    public String description() {
        return "Toggles the option to automatically save and send all files at the end of each session - not just saved or clipped files.";
    }
}
