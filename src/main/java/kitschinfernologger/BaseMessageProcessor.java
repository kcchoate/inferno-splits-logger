package kitschinfernologger;

import net.runelite.api.events.ChatMessage;

import java.util.Arrays;
import java.util.List;

public abstract class BaseMessageProcessor {

    private final List<String> removeFromStringStrings = Arrays.asList("<col=ef1020>","</col>","<col=ff0000>");
    protected String waveSplitsString = "";
    protected String currentWave = null;
    protected String killcount = null;
    protected String duration = "";
    protected String personalBest = "";

    public final void handleMessage(ChatMessage message)
    {
        switch (getMessageType(message)) {
            case FirstWave:
                HandleFirstWaveMessage(message);
                HandleGenericWaveMessage(message);
                break;
            case GenericWave:
                HandleGenericWaveMessage(message);
                break;
            case WaveSplit:
                HandleWaveSplitMessage(message);
                break;
            case Kc:
                HandleKcMessage(message);
                break;
            case Duration:
                HandleDurationMessage(message);
                break;
            case Defeated:
                HandleDefeatedMessage(message);
                break;
            default:
                HandleUnknownMessage(message);
                break;
        }
    }

    protected void HandleFirstWaveMessage(ChatMessage message) {
        reset();
    }

    protected void HandleGenericWaveMessage(ChatMessage message) {
        currentWave = message.getMessage();
        for (String removestring : removeFromStringStrings)
        {
            currentWave=currentWave.replace(removestring,"");
        }

    }

    protected void HandleWaveSplitMessage(ChatMessage message) {
        String chatMessage = message.getMessage();
        for (String removestring : removeFromStringStrings)
        {
            chatMessage = chatMessage.replace(removestring,"");
        }
        waveSplitsString += currentWave + ", " + chatMessage +"\n";
    }

    protected void HandleKcMessage(ChatMessage message) {
        killcount = message.getMessage().replaceAll("\\D+","");
    }

    protected void HandleDurationMessage(ChatMessage message) {
        if (killcount == null) {
            return;
        }
        duration = message.getMessage().split("</")[0].split(">")[1].replace(":",";").replace(".",",");
        personalBest = message.getMessage().split("Personal best: ")[1];

        waveSplitsString += "Duration: " + duration + "\n";
        waveSplitsString += "Personal best: " + personalBest;
    }

    protected void HandleDefeatedMessage(ChatMessage message) {

    }

    protected void HandleUnknownMessage(ChatMessage message) {

    }

    private MessageType getMessageType(ChatMessage message) {
        String text = message.getMessage();
        if (text.startsWith("<col=ef1020>Wave: 1</col>")) {
            return MessageType.FirstWave;
        }
        if (text.startsWith("<col=ef1020>Wave:")) {
            return MessageType.GenericWave;
        }
        if (text.startsWith("<col=ef1020>Wave Split:")) {
            return MessageType.WaveSplit;
        }
        if (text.startsWith("Your TzKal-Zuk kill count is:")) {
            return MessageType.Kc;
        }
        if (text.startsWith("Duration:")) {
            return MessageType.Duration;
        }
        if (text.startsWith("You have been defeated")) {
            return MessageType.Defeated;
        }
        return MessageType.Unknown;
    }

    private void reset() {
        waveSplitsString = "";
        personalBest = "";
        duration = "";
        currentWave= null;
        killcount = null;
    }
}
