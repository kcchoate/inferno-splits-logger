package kitschinfernologger;

import com.google.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.ChatMessage;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static net.runelite.client.RuneLite.RUNELITE_DIR;

@Slf4j
public class FileLoggerMessageProcessor extends BaseMessageProcessor {

    @Inject
    private Client client;

    @Override
    protected void HandleDurationMessage(ChatMessage message) {
        super.HandleDurationMessage(message);

        textfilecreator(killcount, duration);
    }

    @Override
    protected void HandleDefeatedMessage(ChatMessage message) {
        super.HandleDefeatedMessage(message);

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH;mm");
        LocalDateTime now = LocalDateTime.now();
        textfilecreator("0000Failed ",currentWave.replace(":","")+ ", "+ dtf.format(now) );
    }

    private void textfilecreator(String killcount, String duration) {
        File dir = new File(RUNELITE_DIR, "InfernoTimerLogs/" + client.getLocalPlayer().getName());
        dir.mkdirs();

        String fileName = killcount.substring(4) + "KC, " + duration + ".txt";

        try (FileWriter fw = new FileWriter(new File(dir, fileName)))
        {
            fw.write(waveSplitsString);
        }
        catch (IOException ex)
        {
            log.debug("Error writing file: {}", ex.getMessage());
        }
    }
}
