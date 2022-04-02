package kitschinfernologger;

import javax.inject.Inject;
import com.google.inject.Provides;
import net.runelite.api.Client;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import java.util.Arrays;
import java.util.List;

@PluginDescriptor(
        name = "Inferno Splits Logger",
        description = "Saves inferno splits timers to a text file in .runelite/InfernoTimersLogs",
        tags = {"Inferno", "Timers","kitsch"}
)
public class InfernoSplitsLoggerPlugin extends Plugin{

    @Inject
    private Client client;

    @Inject
    private FileLoggerMessageProcessor fileLoggerMessageProcessor;

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
        fileLoggerMessageProcessor.handleMessage(event);
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

