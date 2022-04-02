package kitschinfernologger;

import com.google.common.base.Strings;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.events.ChatMessage;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import javax.inject.Inject;
import java.io.IOException;

import static net.runelite.http.api.RuneLiteAPI.GSON;

@Slf4j
public class DiscordLoggerMessageProcessor extends BaseMessageProcessor {

    @Inject
    private OkHttpClient okHttpClient;

    @Override
    public void onCompletionMessage(ChatMessage message, InfernoState state) {
        sendMessage(getCompletionString(state));
    }

    private String getCompletionString(InfernoState state) {
        return client.getLocalPlayer().getName() + " splits!\n" + state.getSplitsCsv();
    }

    private void sendMessage(String message)
    {
        if (!config.getShouldUploadToDiscord()) {
            return;
        }

        String configUrl = config.getDiscordWebhookUrl();
        if (Strings.isNullOrEmpty(configUrl))
        {
            return;
        }

        HttpUrl url = HttpUrl.parse(configUrl);
        MultipartBody.Builder requestBodyBuilder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("payload_json", GSON.toJson(new WebhookBody(message)));

        RequestBody requestBody = requestBodyBuilder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(requestBody)
                .build();
        sendRequest(request);
    }

    private void sendRequest(Request request)
    {
        okHttpClient.newCall(request).enqueue(new Callback()
        {
            @Override
            public void onFailure(Call call, IOException e)
            {
                log.debug("Error submitting webhook", e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException
            {
                response.close();
            }
        });
    }

    private class WebhookBody {
        private String content;

        public WebhookBody(String content) {
            this.content = content;
        }
    }
}
