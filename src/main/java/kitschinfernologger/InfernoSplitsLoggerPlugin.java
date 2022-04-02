package kitschinfernologger;

import javax.inject.Inject;
import com.google.inject.Provides;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@PluginDescriptor(
        name = "Inferno Splits Logger",
        description = "Saves inferno splits timers to a text file in .runelite/InfernoTimersLogs",
        tags = {"Inferno", "Timers","kitsch"}
)
public class InfernoSplitsLoggerPlugin extends Plugin{

    @Inject
    private FileLoggerMessageProcessor fileLoggerMessageProcessor;

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
            fileLoggerMessageProcessor.OnConfigChange(configChanged);
        }
    }

    @Override
    protected void shutDown() throws Exception {
        fileLoggerMessageProcessor.reset();
    }
}

