package com.echobye.personalhub.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class QwenClient {

    private final RestClient restClient;

    public QwenClient(
            RestClient.Builder restClientBuilder,
            @Value("${DASHSCOPE_API_KEY}") String apiKey
    ) {
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

    public String chat(String userMessage) {

        QwenRequest request = new QwenRequest(
                "qwen3.8-flash",
                List.of(
                        new Message(
                                "system",
                                "You are a helpful assistant."
                        ),
                        new Message(
                                "user",
                                userMessage
                        )
                )
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

            throw new RuntimeException("Qwen 返回结果为空");
        }

        return response
                .choices()
                .get(0)
                .message()
                .content();
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

    private record Choice(
            Message message
    ) {
    }

    private record QwenResponse(
            List<Choice> choices
    ) {
    }
}