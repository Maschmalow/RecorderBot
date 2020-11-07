package net.maschmalow.commands;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

public interface Command {

    void action(String[] args, GuildMessageReceivedEvent e) throws IllegalArgumentException;

    String usage(String prefix);

    String description();

}
