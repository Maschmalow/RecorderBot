package net.maschmalow.commands;


import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.maschmalow.RecorderBot;
import net.maschmalow.commands.audio.ClipCommand;
import net.maschmalow.commands.audio.EchoCommand;
import net.maschmalow.commands.audio.RecordCommand;
import net.maschmalow.commands.misc.JoinCommand;
import net.maschmalow.commands.misc.LeaveCommand;
import net.maschmalow.commands.settings.AutoJoinCommand;
import net.maschmalow.commands.settings.VolumeCommand;
import net.maschmalow.configuration.ServerSettings;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class SlashCommandsManager extends ListenerAdapter {


    public static SlashCommandsManager makeCommandsManager() {
        return new SlashCommandsManager(
                new ClipCommand(),
                new JoinCommand(),
                new LeaveCommand(),
                new VolumeCommand(),
                new EchoCommand(),
                new RecordCommand(),
                new AutoJoinCommand(),
                new AutoJoinCommand());
    }

    private final Map<String, SlashCommandBase> commands = new HashMap<>();

    public SlashCommandsManager(SlashCommandBase... commands) {
        for (SlashCommandBase command : commands) {
            this.commands.put(command.getCommandData().getName(), command);
        }

    }

    public SlashCommandsManager(Collection<? extends SlashCommandBase> commands) {
        this(commands.toArray(new SlashCommandBase[0]));
    }

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event) {
        commands.get(event.getName()).onInteraction(event);
    }

    @Override
    public void onCommandAutoCompleteInteraction(CommandAutoCompleteInteractionEvent event) {
        commands.get(event.getName()).onAutoComplete(event);
    }

    @Override
    public void onReady(ReadyEvent event) {
        //current strategy is to have no global command and use only guild commands
        event.getJDA().updateCommands().queue();
        for (String id : ServerSettings.guilds()) {
            event.getJDA().getGuildById(id).updateCommands().addCommands(
                            commands.values().stream().map(SlashCommandBase::getCommandData).collect(Collectors.toList()))
                    .queue(null, error -> RecorderBot.LOG.warn("could not update commands :  " + error));
        }
    }
}

