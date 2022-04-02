package kitschinfernologger;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.ChatMessage;

import javax.inject.Inject;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public abstract class BaseMessageProcessor {
    @Inject
    InfernoSplitsLoggerConfig config;

    @Inject
    Client client;

    private final Pattern wavePattern = Pattern.compile("(?<=wave: )\\d+", Pattern.CASE_INSENSITIVE);
    private final Pattern killCountPattern = Pattern.compile("(?<=your tzkal-zuk kill count is: )\\d+", Pattern.CASE_INSENSITIVE);
    private final Pattern waveSplitPattern = Pattern.compile("(?<=wave split: )\\d+:\\d+", Pattern.CASE_INSENSITIVE);

    Map<Integer, String> waveSplits = new HashMap<>();
    int currentWave;
    int killCount;
    String duration = "";
    String personalBest = "";

    public final void handleMessage(ChatMessage message)
    {
        final MessageType messageType = getMessageType(message);
        switch (messageType) {
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
        waveSplits.clear();
        personalBest = "";
        duration = "";
        currentWave= 0;
        killCount = 0;
    }

    void HandleFirstWaveMessage(ChatMessage message) {
        reset();
    }

    void HandleGenericWaveMessage(ChatMessage message) {
        Matcher matcher = wavePattern.matcher(message.getMessage());
        if (matcher.find()) {
            currentWave = Integer.parseInt(matcher.group());
        }
    }

    void HandleWaveSplitMessage(ChatMessage message) {
        Matcher matcher = waveSplitPattern.matcher(message.getMessage());
        if (matcher.find()) {
            waveSplits.put(currentWave, matcher.group());
        }
    }

    void HandleKcMessage(ChatMessage message) {
        Matcher matcher = killCountPattern.matcher(message.getMessage());
        if (matcher.find()) {
            killCount = Integer.parseInt(matcher.group());
        }
    }

    void HandleCompletionMessage(ChatMessage message) {
        if (killCount == 0) {
            return;
        }
        duration = message.getMessage().split("</")[0].split(">")[1].replace(":",";").replace(".",",");
        personalBest = message.getMessage().split("Personal best: ")[1];
    }

    void HandleDefeatedMessage(ChatMessage message) {

    }

    void HandleUnknownMessage(ChatMessage message) {

    }

    String getSplitsCsv() {
        StringBuilder sb = new StringBuilder();
        sb.append("Wave,Split");

        for (Map.Entry<Integer, String> split : waveSplits.entrySet()) {
            sb.append('\n');
            sb.append(split.getKey());
            sb.append(split.getValue());
        }

        return sb.toString();
    }

    private MessageType getMessageType(ChatMessage message) {
        String text = message.getMessage().toLowerCase(Locale.ROOT);
        if (text.startsWith("<col=ef1020>wave: 1</col>")) {
            return MessageType.FirstWave;
        }
        if (text.startsWith("<col=ef1020>wave:")) {
            return MessageType.GenericWave;
        }
        if (text.startsWith("<col=ef1020>wave split:")) {
            return MessageType.WaveSplit;
        }
        if (text.startsWith("your tzkal-zuk kill count is:")) {
            return MessageType.Kc;
        }
        if (text.startsWith("duration:")) {
            return MessageType.Completion;
        }
        if (text.startsWith("you have been defeated")) {
            return MessageType.Defeated;
        }
        return MessageType.Unknown;
    }
}
