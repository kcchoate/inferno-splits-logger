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

    @Inject
    InfernoState state;

    private final Pattern wavePattern = Pattern.compile("(?<=wave: )\\d+", Pattern.CASE_INSENSITIVE);
    private final Pattern killCountPattern = Pattern.compile("(?<=your tzkal-zuk kill count is: )\\d+", Pattern.CASE_INSENSITIVE);
    private final Pattern waveSplitPattern = Pattern.compile("(?<=wave split: )\\d+:\\d+", Pattern.CASE_INSENSITIVE);
    private final Pattern durationPattern = Pattern.compile("(?<=duration: )\\d+:\\d+", Pattern.CASE_INSENSITIVE);
    private final Pattern pbPattern = Pattern.compile("(?<=personal best: )\\d+:\\d+", Pattern.CASE_INSENSITIVE);

    public final void handleMessage(ChatMessage message)
    {
        try {
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
        catch (Exception ex) {
            log.error("Error processing message: {}", ex);
        }
    }

    public void reset() {
        state.reset();
    }

    void HandleFirstWaveMessage(ChatMessage message) {
        reset();
    }

    void HandleGenericWaveMessage(ChatMessage message) {
        Matcher matcher = wavePattern.matcher(message.getMessage());
        if (matcher.find()) {
            state.setCurrentWave(Integer.parseInt(matcher.group()));
        }
    }

    void HandleWaveSplitMessage(ChatMessage message) {
        Matcher matcher = waveSplitPattern.matcher(message.getMessage());
        if (matcher.find()) {
            state.addSplit(matcher.group());
        }
    }

    void HandleKcMessage(ChatMessage message) {
        Matcher matcher = killCountPattern.matcher(message.getMessage());
        if (matcher.find()) {
            state.setKillCount(Integer.parseInt(matcher.group()));
        }
    }

    void HandleCompletionMessage(ChatMessage message) {
        final String text = message.getMessage();
        Matcher matcher = durationPattern.matcher(text);
        if (!matcher.find()) {
            return;
        }

        state.setDuration(matcher.group());
        if (text.toLowerCase(Locale.ROOT).contains("new personal best")) {
            state.setPersonalBest(state.getDuration());
        }
        else {
            Matcher pbMatcher = pbPattern.matcher(text);
            if (pbMatcher.find()) {
                state.setPersonalBest(pbMatcher.group());
            }
        }
    }

    void HandleDefeatedMessage(ChatMessage message) {

    }

    void HandleUnknownMessage(ChatMessage message) {

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
