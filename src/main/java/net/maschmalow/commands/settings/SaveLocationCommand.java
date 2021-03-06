package net.maschmalow.commands.settings;

import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.maschmalow.Utilities;
import net.maschmalow.commands.Command;
import net.maschmalow.configuration.ServerSettings;

/*
    Disabled for now
 */
public class SaveLocationCommand implements Command {


    @Override
    public void action(String[] args, GuildMessageReceivedEvent e) throws IllegalArgumentException {
        if(args.length > 1)
            throw new IllegalArgumentException("This command requires one or zero argument");

        TextChannel newDefault = Utilities.findTextChannel((args.length == 0)? null: args[0], e);

        //ServerSettings.get(e.getGuild()).defaultTextChannel = newDefault.getId();
        ServerSettings.write();
        Utilities.sendMessage(e.getChannel(), "Now defaulting to the " + e.getChannel().getName() + " text channel");

    }

    @Override
    public String usage(String prefix) {
        return prefix + "saveLocation | " + prefix + "saveLocation [text channel name]";
    }

    @Override
    public String description() {
        return "Sets the text channel of message or the text channel specified as the default location to send files";
    }

}
