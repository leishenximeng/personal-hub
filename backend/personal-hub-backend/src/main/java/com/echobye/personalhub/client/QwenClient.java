package com.echobye.personalhub.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;

import com.echobye.personalhub.dto.ChatMessage;

@Component
public class QwenClient {

    private final RestClient restClient;
    private final JsonMapper jsonMapper;

    private List<Message> buildMessages(
            List<ChatMessage> chatMessages
    ) {

        List<Message> messages =
                new java.util.ArrayList<>();

        // 系统提示词
        messages.add(
                new Message(
                        "system",
                        "You are a helpful assistant."
                )
        );

        // 历史聊天记录
        for (ChatMessage chatMessage : chatMessages) {

            messages.add(
                    new Message(
                            chatMessage.role(),
                            chatMessage.content()
                    )
            );
        }

        return messages;
    }

    public QwenClient(
            RestClient.Builder restClientBuilder,
            JsonMapper jsonMapper,
            @Value("${DASHSCOPE_API_KEY}") String apiKey
    ) {
        this.jsonMapper = jsonMapper;

        this.restClient = restClientBuilder
                .baseUrl("https://dashscope.aliyuncs.com/compatible-mode/v1")
                .defaultHeader(
                        HttpHeaders.AUTHORIZATION,
                        "Bearer " + apiKey
                )
                .defaultHeader(
                        HttpHeaders.CONTENT_TYPE,
                        MediaType.APPLICATION_JSON_VALUE
                )
                .build();
    }

    /**
     * 普通非流式聊天
     */
    public String chat(
            List<ChatMessage> chatMessages
    ) {

        QwenRequest request =
                new QwenRequest(
                        "qwen3.8-flash",
                        buildMessages(chatMessages)
                );

        QwenResponse response = restClient
                .post()
                .uri("/chat/completions")
                .body(request)
                .retrieve()
                .body(QwenResponse.class);

        if (response == null
                || response.choices() == null
                || response.choices().isEmpty()
                || response.choices().get(0).message() == null) {

            throw new RuntimeException(
                    "Qwen 返回结果为空"
            );
        }

        return response
                .choices()
                .get(0)
                .message()
                .content();
    }

    /**
     * 流式聊天
     *
     * Qwen 每生成一小段文字，就调用一次 onChunk.accept(...)
     */
    public void streamChat(
            List<ChatMessage> chatMessages,
            Consumer<String> onChunk
    ) {

        QwenStreamRequest request =
                new QwenStreamRequest(
                        "qwen3.8-flash",
                        buildMessages(chatMessages),
                        true
                );

        // 下面原来的流式读取逻辑保持不变

        restClient
                .post()
                .uri("/chat/completions")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .body(request)
                .exchange((httpRequest, response) -> {

                    if (response.getStatusCode().isError()) {
                        throw new RuntimeException(
                                "Qwen 请求失败：" + response.getStatusCode()
                        );
                    }

                    try (
                            BufferedReader reader =
                                    new BufferedReader(
                                            new InputStreamReader(
                                                    response.getBody(),
                                                    StandardCharsets.UTF_8
                                            )
                                    )
                    ) {

                        String line;

                        while ((line = reader.readLine()) != null) {

                            // Qwen SSE 数据类似：
                            // data: {"choices":[...]}
                            if (!line.startsWith("data:")) {
                                continue;
                            }

                            String data = line.substring(5).trim();

                            // 流结束
                            if ("[DONE]".equals(data)) {
                                break;
                            }

                            JsonNode root = jsonMapper.readTree(data);

                            JsonNode contentNode = root
                                    .path("choices")
                                    .path(0)
                                    .path("delta")
                                    .path("content");

                            if (!contentNode.isMissingNode()
                                    && !contentNode.isNull()) {

                                String content = contentNode.asText();

                                if (!content.isEmpty()) {
                                    onChunk.accept(content);
                                }
                            }
                        }

                    } catch (Exception e) {
                        throw new RuntimeException(
                                "读取 Qwen 流式响应失败",
                                e
                        );
                    }

                    return null;
                });
    }

    private record Message(
            String role,
            String content
    ) {
    }

    private record QwenRequest(
            String model,
            List<Message> messages
    ) {
    }

    private record QwenStreamRequest(
            String model,
            List<Message> messages,
            boolean stream
    ) {
    }

    private record Choice(
            Message message
    ) {
    }

    private record QwenResponse(
            List<Choice> choices
    ) {
    }
}