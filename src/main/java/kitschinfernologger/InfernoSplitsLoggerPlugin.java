package kitschinfernologger;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import static net.runelite.client.RuneLite.RUNELITE_DIR;
import java.io.*;
import java.util.Arrays;
import java.util.List;


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

    @Subscribe
    private void onChatMessage(ChatMessage event){
        if (event.getMessage().startsWith("<col=ef1020>Wave: 1</col>")) {
            reset();
        }
        if (event.getMessage().startsWith("<col=ef1020>Wave:")){
            currentWave = event.getMessage();
            for (String removestring : removeFromStringStrings)
            {
                currentWave=currentWave.replace(removestring,"");
            }
        }

        if (event.getMessage().startsWith("<col=ef1020>Wave Split:")){
            String chatMessage = event.getMessage();
            for (String removestring : removeFromStringStrings)
            {
                chatMessage = chatMessage.replace(removestring,"");
            }
            waveSplitsString += currentWave + ", " + chatMessage +"\n";
        }

        if (event.getMessage().startsWith("Your TzKal-Zuk kill count is:")){
            killcount = event.getMessage().replaceAll("\\D+","");
        }
        if (event.getMessage().startsWith("Duration:") && killcount!=null){
            duration = event.getMessage().split("\\.")[0].split(">")[1].replace(":",";");
            personalBest = event.getMessage().split("Personal best: ")[1];

            waveSplitsString += "Duration: " + duration + "\n";
            waveSplitsString += "Personal best: " + personalBest;

            textfilecreator(killcount, duration);
        }
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

