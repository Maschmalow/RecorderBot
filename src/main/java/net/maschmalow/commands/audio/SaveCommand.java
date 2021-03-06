package net.maschmalow.commands.audio;

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.maschmalow.Utilities;
import net.maschmalow.commands.Command;


public class SaveCommand implements Command {


    @Override
    public void action(String[] args, GuildMessageReceivedEvent e) {
        if(args.length > 1)
            throw new IllegalArgumentException("This command require no more than one argument");

        if(e.getGuild().getAudioManager().getConnectedChannel() == null){
            Utilities.sendMessage(e.getChannel(), "I wasn't recording!");
            return;
        }


        //TextChannel savingChannel = Utilities.findTextChannel((args.length == 0) ? null : args[0], e);

        Utilities.saveToFile(e.getGuild(),e.getAuthor().getName(),null);

    }

    @Override
    public String usage(String prefix) {
        return prefix + "save | " + prefix + "save [text channel output]";
    }

    @Override
    public String description() {
        return "Saves the current recording and outputs it to the current or specified text chats";
    }


}
