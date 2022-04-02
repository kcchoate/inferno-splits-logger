package kitschinfernologger;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class InfernoState {
    private int currentWave;
    private int killCount;
    private String duration;
    private String personalBest;
    Map<Integer, String> waveSplits = new HashMap<>();

    public void reset() {
        waveSplits.clear();
        setPersonalBest("");
        setDuration("");
        setCurrentWave(0);
        setKillCount(0);
    }

    public void addSplit(String split) {
        waveSplits.put(currentWave, split);
    }

    String getSplitsCsv() {
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

}
