package net.maschmalow.commands.audio;

import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.maschmalow.Utilities;
import net.maschmalow.commands.Command;
import net.maschmalow.recorderlib.RecorderAudioHandler;
import net.maschmalow.recorderlib.PCMBufferSender;


public class EchoCommand implements Command {



    @Override
    public void action(String[] args, GuildMessageReceivedEvent e) {
        if (args.length != 1)
            throw new IllegalArgumentException("This command require exactly one argument");

        if (e.getGuild().getAudioManager().getConnectedChannel() == null) {
            Utilities.sendMessage(e.getChannel(), "I wasn't recording!");
            return;
        }

        int time = Utilities.parseUInt(args[0]);

        RecorderAudioHandler ah = (RecorderAudioHandler) e.getGuild().getAudioManager().getReceiveHandler();
        byte[] voiceData;
        if (ah == null || (voiceData = ah.getUncompVoice(time)) == null) {
            Utilities.sendMessage(e.getChannel(), "I wasn't recording!");
            return;
        }

        PCMBufferSender as = new PCMBufferSender(voiceData);
        e.getGuild().getAudioManager().setSendingHandler(as);

    }

    @Override
    public String usage(String prefix) {
        return prefix + "echo [seconds]";
    }

    @Override
    public String description() {
        return "Echos back the input number of seconds of the recording into the voice channel (max 120 seconds)";
    }


}
