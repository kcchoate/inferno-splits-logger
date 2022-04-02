package kitschinfernologger;

import net.runelite.api.events.ChatMessage;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class InfernoState {
    private final Pattern wavePattern = Pattern.compile("(?<=wave: )\\d+", Pattern.CASE_INSENSITIVE);
    private final Pattern killCountPattern = Pattern.compile("(?<=your tzkal-zuk kill count is: )\\d+", Pattern.CASE_INSENSITIVE);
    private final Pattern waveSplitPattern = Pattern.compile("(?<=wave split: )\\d+:\\d+", Pattern.CASE_INSENSITIVE);
    private final Pattern durationPattern = Pattern.compile("(?<=duration: )\\d+:\\d+", Pattern.CASE_INSENSITIVE);
    private final Pattern pbPattern = Pattern.compile("(?<=personal best: )\\d+:\\d+", Pattern.CASE_INSENSITIVE);

    private int currentWave;
    private int killCount;
    private String duration;
    private String personalBest;
    Map<Integer, String> waveSplits = new HashMap<>();

    public int getCurrentWave() {
        return currentWave;
    }

    public void setCurrentWave(int newValue) {
        currentWave = newValue;
    }

    public int getKillCount() {
        return killCount;
    }

    public void setKillCount(int newValue) {
        killCount = newValue;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String newValue) {
        duration = newValue;
    }

    public String getPersonalBest() {
        return personalBest;
    }

    public void setPersonalBest(String newValue) {
        personalBest = newValue;
    }

    public String getSplitsCsv() {
        StringBuilder sb = new StringBuilder();
        sb.append("Wave,Split");
        sb.append('\n');

        for (Map.Entry<Integer, String> split : waveSplits.entrySet()) {
            sb.append(split.getKey());
            sb.append(',');
            sb.append(split.getValue());
            sb.append('\n');
        }

        sb.append("end,");
        sb.append(duration);
        sb.append('\n');

        return sb.toString();
    }

    public void addSplit(String split) {
        waveSplits.put(currentWave, split);
    }

    public MessageType processMessage(ChatMessage message) {
        final MessageType messageType = getMessageType(message);

        switch (messageType) {
            case FirstWave:
            case GenericWave:
                handleGenericWaveMessage(message);
                break;
            case WaveSplit:
                handleWaveSplitMessage(message);
                break;
            case Kc:
                handleKcMessage(message);
                break;
            case Completion:
                handleCompletionMessage(message);
                break;
        }

        return messageType;
    }

    private void handleGenericWaveMessage(ChatMessage message) {
        Matcher matcher = wavePattern.matcher(message.getMessage());
        if (matcher.find()) {
            setCurrentWave(Integer.parseInt(matcher.group()));
        }
    }

    private void handleWaveSplitMessage(ChatMessage message) {
        Matcher matcher = waveSplitPattern.matcher(message.getMessage());
        if (matcher.find()) {
            addSplit(matcher.group());
        }
    }

    private void handleKcMessage(ChatMessage message) {
        Matcher matcher = killCountPattern.matcher(message.getMessage());
        if (matcher.find()) {
            setKillCount(Integer.parseInt(matcher.group()));
        }
    }

    private void handleCompletionMessage(ChatMessage message) {
        final String text = message.getMessage();
        Matcher matcher = durationPattern.matcher(text);
        if (!matcher.find()) {
            return;
        }

        setDuration(matcher.group());
        if (text.toLowerCase(Locale.ROOT).contains("new personal best")) {
            setPersonalBest(getDuration());
        }
        else {
            Matcher pbMatcher = pbPattern.matcher(text);
            if (pbMatcher.find()) {
                setPersonalBest(pbMatcher.group());
            }
        }
    }

    public void reset() {
        waveSplits.clear();
        setPersonalBest("");
        setDuration("");
        setCurrentWave(0);
        setKillCount(0);
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
