package kitschinfernologger;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.ChatMessage;

import javax.inject.Inject;

@Slf4j
public final class MessageProcessorCollection {
    @Inject InfernoState state;

    @Inject DiscordLoggerMessageProcessor discordLogger;
    @Inject FileLoggerMessageProcessor fileLogger;

    public void handleMessage(ChatMessage message)
    {
        try {
            final MessageType messageType = state.processMessage(message);

            switch (messageType) {
                case FirstWave:
                    handleFirstWaveMessage(message);
                    handleGenericWaveMessage(message);
                    break;
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
                case Defeated:
                    handleDefeatedMessage(message);
                    break;
                default:
                    handleUnknownMessage(message);
                    break;
            }
        }
        catch (Exception ex) {
            log.error("Error processing message: {}", ex);
        }
    }

    public void reset() {
        state.reset();
    }

    private void handleFirstWaveMessage(ChatMessage message) {
        discordLogger.HandleFirstWaveMessage(message, state);
        fileLogger.HandleFirstWaveMessage(message, state);
    }

    private void handleGenericWaveMessage(ChatMessage message) {
        discordLogger.HandleGenericWaveMessage(message, state);
        fileLogger.HandleGenericWaveMessage(message, state);
    }

    private void handleWaveSplitMessage(ChatMessage message) {
        discordLogger.HandleWaveSplitMessage(message, state);
        fileLogger.HandleWaveSplitMessage(message, state);
    }

    private void handleKcMessage(ChatMessage message) {
        discordLogger.HandleKcMessage(message, state);
        fileLogger.HandleKcMessage(message, state);
    }

    private void handleCompletionMessage(ChatMessage message) {
        discordLogger.onCompletionMessage(message, state);
        fileLogger.onCompletionMessage(message, state);
    }

    private void handleDefeatedMessage(ChatMessage message) {
        discordLogger.onDefeatedMessage(message, state);
        fileLogger.onDefeatedMessage(message, state);
    }

    private void handleUnknownMessage(ChatMessage message) {
        discordLogger.HandleUnknownMessage(message, state);
        fileLogger.HandleUnknownMessage(message, state);
    }
}
