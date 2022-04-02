package kitschinfernologger;

import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.ChatMessage;

import javax.inject.Inject;

@Slf4j
public abstract class BaseMessageProcessor {
    @Inject
    InfernoSplitsLoggerConfig config;

    @Inject
    Client client;

    public void reset() {
    }

    public void HandleFirstWaveMessage(ChatMessage message, InfernoState state) {
        reset();
    }

    public void HandleGenericWaveMessage(ChatMessage message, InfernoState state) {
    }

    public void HandleWaveSplitMessage(ChatMessage message, InfernoState state) {
    }

    public void HandleKcMessage(ChatMessage message, InfernoState state) {
    }

    public void onCompletionMessage(ChatMessage message, InfernoState state) {
    }

    public void onDefeatedMessage(ChatMessage message, InfernoState state) {
    }

    public void HandleUnknownMessage(ChatMessage message, InfernoState state) {
    }
}
