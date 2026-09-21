package com.echobye.personalhub.service;

import com.echobye.personalhub.client.QwenClient;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import com.echobye.personalhub.dto.ChatMessage;

import java.util.List;

import java.io.IOException;

@Service
public class ChatService {

    private final QwenClient qwenClient;

    public ChatService(QwenClient qwenClient) {
        this.qwenClient = qwenClient;
    }

    /**
     * 普通非流式聊天
     */
    public String chat(
            List<ChatMessage> messages
    ) {
        return qwenClient.chat(messages);
    }

    /**
     * 流式聊天
     */
    public SseEmitter streamChat(
            List<ChatMessage> messages
    ) {

        SseEmitter emitter =
                new SseEmitter(300_000L);

        Thread.startVirtualThread(() -> {

            try {

                qwenClient.streamChat(
                        messages,
                        chunk -> {

                            try {

                                emitter.send(
                                        SseEmitter
                                                .event()
                                                .data(chunk)
                                );

                            } catch (IOException e) {

                                throw new RuntimeException(e);
                            }
                        }
                );

                emitter.send(
                        SseEmitter
                                .event()
                                .name("done")
                                .data("[DONE]")
                );

                emitter.complete();

            } catch (Exception e) {

                emitter.completeWithError(e);
            }
        });

        return emitter;
    }
}