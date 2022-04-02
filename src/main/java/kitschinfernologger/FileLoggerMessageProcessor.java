package kitschinfernologger;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.ChatMessage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static net.runelite.client.RuneLite.RUNELITE_DIR;

@Slf4j
public class FileLoggerMessageProcessor extends BaseMessageProcessor {
    @Override
    public void onCompletionMessage(ChatMessage message, InfernoState state) {
        writeSplitsToFile(state);
    }

    @Override
    public void onDefeatedMessage(ChatMessage message, InfernoState state) {
        writeSplitsToFile(state);
    }

    private void writeSplitsToFile(InfernoState state) {
        if (!config.getShouldWriteToFile()) {
            return;
        }

        File dir = new File(RUNELITE_DIR, "InfernoTimerLogs/" + client.getLocalPlayer().getName());
        dir.mkdirs();

        String fileName = getFileName(state);
        try (FileWriter fw = new FileWriter(new File(dir, fileName)))
        {
            fw.write(state.getSplitsCsv());
        }
        catch (IOException ex)
        {
            log.debug("Error writing file: {}", ex.getMessage());
        }
    }

    private String getFileName(InfernoState state) {
        if (state.getKillCount() == 0) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH;mm");
            LocalDateTime now = LocalDateTime.now();
            return  "Failed KC, Wave " + state.getCurrentWave() + dtf.format(now) + ".csv";
        }
        return state.getKillCount() + "KC, " + state.getDuration() + ".csv";
    }
}
