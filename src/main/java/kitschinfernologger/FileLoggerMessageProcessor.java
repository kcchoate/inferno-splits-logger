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
    protected void HandleCompletionMessage(ChatMessage message) {
        super.HandleCompletionMessage(message);
        writeSplitsToFile();
    }

    @Override
    protected void HandleDefeatedMessage(ChatMessage message) {
        super.HandleDefeatedMessage(message);
        writeSplitsToFile();
    }

    private void writeSplitsToFile() {
        if (!config.getShouldWriteToFile()) {
            return;
        }

        File dir = new File(RUNELITE_DIR, "InfernoTimerLogs/" + client.getLocalPlayer().getName());
        dir.mkdirs();

        String fileName = getFileName();

        try (FileWriter fw = new FileWriter(new File(dir, fileName)))
        {
            fw.write(waveSplitsString);
        }
        catch (IOException ex)
        {
            log.debug("Error writing file: {}", ex.getMessage());
        }
    }

    private String getFileName() {
        if (killCount == 0) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH;mm");
            LocalDateTime now = LocalDateTime.now();
            return  "Failed KC, Wave " + currentWave + dtf.format(now) + ".csv";
        }
        return killCount + "KC, " + duration + ".csv";
    }
}
