package kitschinfernologger;
import javax.inject.Inject;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import static net.runelite.client.RuneLite.RUNELITE_DIR;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;


@Slf4j
@PluginDescriptor(
        name = "Inferno Splits Logger",
        description = "Saves inferno splits timers to a text file in .runelite/InfernoTimersLogs",
        tags = {"Inferno", "Timers","kitsch"}
)
public class InfernoSplitsLoggerPlugin extends Plugin{

    @Inject
    private Client client;

    List<String> removeFromStringStrings = Arrays.asList("<col=ef1020>","</col>","<col=ff0000>");
    String waveSplitsString = "";
    String currentWave = null;
    String killcount = null;
    String duration = "";
    String personalBest = "";
    String discordWebhookUrl = "";
    boolean shouldUploadToDiscord = false;

    @Inject
    private InfernoSplitsLoggerConfig config;

    @Subscribe
    private void onChatMessage(ChatMessage event){
        HandleFirstWaveMessage(event);
        HandleWaveMessage(event);
        HandleWaveSplitMessage(event);
        HandleKcMessage(event);
        HandleDurationMessage(event);
        HandleDefeatedMessage(event);
    }

    @Provides
    InfernoSplitsLoggerConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(InfernoSplitsLoggerConfig.class);
    }

    @Subscribe
    public void onConfigChanged(ConfigChanged configChanged)
    {
        if (configChanged.getGroup().equalsIgnoreCase(InfernoSplitsLoggerConfig.GROUP))
        {
            discordWebhookUrl = config.getDiscordWebhookUrl();
            shouldUploadToDiscord = config.getShouldUploadToDiscord();
        }
    }

    private void HandleFirstWaveMessage(ChatMessage event) {
        if (event.getMessage().startsWith("<col=ef1020>Wave: 1</col>")) {
            reset();
        }
    }

    private void HandleWaveMessage(ChatMessage event) {
        if (!event.getMessage().startsWith("<col=ef1020>Wave:")) {
            return;
        }
        currentWave = event.getMessage();
        for (String removestring : removeFromStringStrings)
        {
            currentWave=currentWave.replace(removestring,"");
        }
    }

    private void HandleWaveSplitMessage(ChatMessage event) {
        if (!event.getMessage().startsWith("<col=ef1020>Wave Split:")) {
            return;
        }

        String chatMessage = event.getMessage();
        for (String removestring : removeFromStringStrings)
        {
            chatMessage = chatMessage.replace(removestring,"");
        }
        waveSplitsString += currentWave + ", " + chatMessage +"\n";
    }

    private void HandleKcMessage(ChatMessage event) {
        if (!event.getMessage().startsWith("Your TzKal-Zuk kill count is:")) {
            return;
        }

        killcount = event.getMessage().replaceAll("\\D+","");
    }

    private void HandleDurationMessage(ChatMessage event) {
        if (!event.getMessage().startsWith("Duration:") || killcount == null) {
            return;
        }
        duration = event.getMessage().split("</")[0].split(">")[1].replace(":",";").replace(".",",");
        personalBest = event.getMessage().split("Personal best: ")[1];

        waveSplitsString += "Duration: " + duration + "\n";
        waveSplitsString += "Personal best: " + personalBest;

        textfilecreator(killcount, duration);
    }

    private void HandleDefeatedMessage(ChatMessage event) {
        if (!event.getMessage().startsWith("You have been defeated") || currentWave == null) {
            return;
        }
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

        reset();
    }


    @Override
    protected void shutDown() throws Exception {
        reset();
    }

    private void reset() {
        waveSplitsString = "";
        personalBest = "";
        duration = "";
        currentWave= null;
        killcount = null;
    }
}

