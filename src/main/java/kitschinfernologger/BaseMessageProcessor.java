package kitschinfernologger;

import net.runelite.api.Client;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.events.ConfigChanged;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

public abstract class BaseMessageProcessor {
    @Inject
    InfernoSplitsLoggerConfig config;

    @Inject
    Client client;

    private final List<String> removeFromStringStrings = Arrays.asList("<col=ef1020>","</col>","<col=ff0000>");
    String waveSplitsString = "";
    String currentWave = null;
    String killcount = null;
    String duration = "";
    String personalBest = "";

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
            case Completion:
                HandleCompletionMessage(message);
                break;
            case Defeated:
                HandleDefeatedMessage(message);
                break;
            default:
                HandleUnknownMessage(message);
                break;
        }
    }

    public void reset() {
        waveSplitsString = "";
        personalBest = "";
        duration = "";
        currentWave= null;
        killcount = null;
    }

    void HandleFirstWaveMessage(ChatMessage message) {
        reset();
    }

    void HandleGenericWaveMessage(ChatMessage message) {
        currentWave = message.getMessage();
        for (String removestring : removeFromStringStrings)
        {
            currentWave=currentWave.replace(removestring,"");
        }

    }

    void HandleWaveSplitMessage(ChatMessage message) {
        String chatMessage = message.getMessage();
        for (String removestring : removeFromStringStrings)
        {
            chatMessage = chatMessage.replace(removestring,"");
        }
        waveSplitsString += currentWave + ", " + chatMessage +"\n";
    }

    void HandleKcMessage(ChatMessage message) {
        killcount = message.getMessage().replaceAll("\\D+","");
    }

    void HandleCompletionMessage(ChatMessage message) {
        if (killcount == null) {
            return;
        }
        duration = message.getMessage().split("</")[0].split(">")[1].replace(":",";").replace(".",",");
        personalBest = message.getMessage().split("Personal best: ")[1];

        waveSplitsString += "Duration: " + duration + "\n";
        waveSplitsString += "Personal best: " + personalBest;
    }

    void HandleDefeatedMessage(ChatMessage message) {

    }

    void HandleUnknownMessage(ChatMessage message) {

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
            return MessageType.Completion;
        }
        if (text.startsWith("You have been defeated")) {
            return MessageType.Defeated;
        }
        return MessageType.Unknown;
    }
}
