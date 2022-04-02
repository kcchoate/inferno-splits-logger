package kitschinfernologger;

import net.runelite.api.events.ChatMessage;

public abstract class BaseMessageProcessor {
    public void reset() { }
    public void onFirstWaveMessage(ChatMessage message, InfernoState state) { }
    public void onGenericWaveMessage(ChatMessage message, InfernoState state) { }
    public void onWaveSplitMessage(ChatMessage message, InfernoState state) { }
    public void onKcMessage(ChatMessage message, InfernoState state) { }
    public void onCompletionMessage(ChatMessage message, InfernoState state) { }
    public void onDefeatedMessage(ChatMessage message, InfernoState state) { }
    public void onUnknownMessage(ChatMessage message, InfernoState state) { }
}
