package kitschinfernologger;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(InfernoSplitsLoggerConfig.GROUP)
public interface InfernoSplitsLoggerConfig extends Config
{
    String GROUP = "infernosplitslogger";

    @ConfigItem(
            keyName = "shouldUploadToDiscord",
            name = "Upload to Discord?",
            description = "Enable to upload your splits to a discord channel using webhooks"
    )
    default boolean getShouldUploadToDiscord()
    {
        return false;
    }

    @ConfigItem(
            keyName = "discordWebhookUrl",
            name = "Webhook URL",
            description = "The Discord Webhook URL to send messages to"
    )
    String getDiscordWebhookUrl();

}
