package net.maschmalow.commands.misc;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.maschmalow.Utilities;
import net.maschmalow.commands.Command;
import net.maschmalow.commands.CommandHandler;
import net.maschmalow.configuration.ServerSettings;

import java.awt.*;
import java.util.ArrayList;
import java.util.Map;


public class HelpCommand implements Command {



    @Override
    public void action(String[] args, GuildMessageReceivedEvent e) {

        EmbedBuilder embed = new EmbedBuilder();
        embed.setAuthor("RecorderBot", "https://github.com/Maschmalow/recorderbot", e.getJDA().getSelfUser().getAvatarUrl());
        embed.setColor(Color.RED);
        embed.setTitle("This is experimental software.");
        embed.setDescription("Stripped down & reworked version of \'Throw Voice\', by Maschmalow.\n" +
                "See https://github.com/guacamoledragon/throw-voice");
        embed.setThumbnail("http://www.freeiconspng.com/uploads/information-icon-5.png");
        embed.setFooter("Replace brackets [] with item specified. Vertical bar | means 'or', either side of bar is valid choice.", null);
        embed.addBlankField(false);


        for (Map.Entry<String, Command> cmdEntry : CommandHandler.commands.entrySet()) {
            Command cmd = cmdEntry.getValue();

            String prefix = ServerSettings.get(e.getGuild()).prefix;

            ArrayList<String> aliases = new ArrayList<>();
            for (Map.Entry<String, String> entry : ServerSettings.get(e.getGuild()).aliases.entrySet()) {
                if (entry.getValue().equals(cmdEntry.getKey()))
                    aliases.add(entry.getKey());
            }

            if (aliases.size() == 0)
                embed.addField(cmd.usage(prefix), cmd.description(), true);
            else {
                StringBuilder description = new StringBuilder();
                description.append("Aliases: ");
                for (String alias : aliases)
                    description.append("`").append(alias).append("`, ");

                //remove extra comma
                description = new StringBuilder(description.substring(0, description.toString().lastIndexOf(',')));
                description.append(". ").append(cmd.description());
                embed.addField(cmd.usage(prefix), description.toString(), true);
            }
        }

        Utilities.sendMessage(e.getChannel(), "Check your DM's!");

        e.getAuthor().openPrivateChannel().queue(privateChannel -> privateChannel.sendMessage(embed.build()).queue());
    }

    @Override
    public String usage(String prefix) {
        return prefix + "help";
    }

    @Override
    public String description() {
        return "Shows all commands and their usages";
    }


}
