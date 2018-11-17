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

import java.util.Arrays;
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
        GuildSettings settings = ServerSettings.get(event.getGuild());

        String[] args = parseArgs(event);
        Command command = commands.get(args[0]);
        if(command == null)
            command = commands.get(settings.aliases.get(args[0]));
        if(command == null)
            return;

        try {
            command.action(Arrays.copyOfRange(args, 1, args.length), event);
        } catch(IllegalArgumentException e) {
            Utilities.sendMessage(event.getChannel(), e.getMessage()+"\n"+command.usage(settings.prefix));
        }

    }

    private static String[] parseArgs(GuildMessageReceivedEvent event) {
        String cmd = event.getMessage().getContentStripped();
        cmd = cmd.substring(ServerSettings.get(event.getGuild()).prefix.length());
        return cmd.split(" ");
    }

}

