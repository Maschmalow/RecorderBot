package net.maschmalow.commands;

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;

public abstract class SlashCommandBase  {

    /**
     * MUST BE a return value from Commands.slash()
     *
     * @return CommandData
     */
    public  abstract CommandData getCommandData();

    public abstract void onInteraction(SlashCommandInteractionEvent e);

    /**
     * override if the command supports autocompletion
     * @param e
     */
    public void onAutoComplete(CommandAutoCompleteInteractionEvent e) {   }


}
