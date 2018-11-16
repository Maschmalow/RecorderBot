package net.maschmalow.commands;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.maschmalow.Utilities;
import net.maschmalow.commands.audio.ClipCommand;
import net.maschmalow.commands.audio.EchoCommand;
import net.maschmalow.commands.audio.MessageInABottleCommand;
import net.maschmalow.commands.audio.SaveCommand;
import net.maschmalow.commands.misc.HelpCommand;
import net.maschmalow.commands.misc.JoinCommand;
import net.maschmalow.commands.misc.LeaveCommand;
import net.maschmalow.commands.settings.*;
import net.maschmalow.configuration.GuildSettings;
import net.maschmalow.configuration.ServerSettings;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

public class CommandHandler {
    public static final Map<String, Command> commands = Map.ofEntries(
        Map.entry("help", new HelpCommand()),

        Map.entry("join", new JoinCommand()),
        Map.entry("leave", new LeaveCommand()),

        Map.entry("save", new SaveCommand()),
        Map.entry("clip", new ClipCommand()),
        Map.entry("echo", new EchoCommand()),
        Map.entry("miab", new MessageInABottleCommand()),

        Map.entry("autojoin", new AutoJoinCommand()),
        Map.entry("autoleave", new AutoLeaveCommand()),

        Map.entry("prefix", new PrefixCommand()),
        Map.entry("alias", new AliasCommand()),
        Map.entry("removealias", new RemoveAliasCommand()),
        Map.entry("volume", new VolumeCommand()),
        Map.entry("autosave", new AutoSaveCommand())
    );


    public static void handleCommand(GuildMessageReceivedEvent event) {
        CommandContainer cmd = new CommandContainer(event);
        GuildSettings settings = ServerSettings.get(event.getGuild());

        Command command = commands.get(cmd.invoke);
        if(command == null)
            command = commands.get(settings.aliases.get(cmd.invoke));
        if(command == null)
            return;

        try {
            command.action(cmd.args, cmd.e);
        } catch(IllegalArgumentException e) {
            Utilities.sendMessage(event.getChannel(), e.getMessage()+"\n"+command.usage(settings.prefix));
        }

    }

    private static String[] parseArgs(GuildMessageReceivedEvent event) {
        String cmd = event.getMessage().getContentStripped();
        cmd = cmd.substring(ServerSettings.get(event.getGuild()).prefix.length());
        return cmd.split(" ");
    }


    private static class CommandContainer {
        public final String raw;
        public final String beheaded;
        public final String[] splitBeheaded;
        public final String invoke;
        public final String[] args;
        public final GuildMessageReceivedEvent e;

        public CommandContainer(GuildMessageReceivedEvent event) {
            this.raw = event.getMessage().getContentRaw().toLowerCase();
            ArrayList<String> split = new ArrayList<>();
            this.beheaded = raw.substring(1);
            this.splitBeheaded = beheaded.split(" ");
            Collections.addAll(split, splitBeheaded);
            this.invoke = split.get(0);
            this.args = new String[split.size() - 1];
            split.subList(1, split.size()).toArray(args);
            this.e = event;
        }
    }
}

