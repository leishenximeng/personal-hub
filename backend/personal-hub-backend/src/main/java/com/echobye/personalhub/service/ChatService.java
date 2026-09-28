package com.echobye.personalhub.service;

import com.echobye.personalhub.client.QwenClient;
import com.echobye.personalhub.entity.MessageRole;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
public class ChatService {

    private final QwenClient qwenClient;
    private final ConversationService conversationService;

    public ChatService(
            QwenClient qwenClient,
            ConversationService conversationService
    ) {
        this.qwenClient = qwenClient;
        this.conversationService =
                conversationService;
    }

    public String chat(
            Long conversationId,
            String message
    ) {

        // ① 先保存用户消息
        conversationService.saveMessage(
                conversationId,
                MessageRole.USER,
                message
        );

        conversationService.updateTitleIfNeeded(
                conversationId,
                message
        );

        // ② 从数据库获取完整上下文
        var history =
                conversationService
                        .getChatHistory(
                                conversationId
                        );

        // ③ 调用 AI
        String reply =
                qwenClient.chat(history);

        // ④ 保存 AI 回答
        conversationService.saveMessage(
                conversationId,
                MessageRole.ASSISTANT,
                reply
        );

        return reply;
    }

    public SseEmitter streamChat(
            Long conversationId,
            String message
    ) {

        SseEmitter emitter =
                new SseEmitter(300_000L);

        Thread.startVirtualThread(() -> {

            try {

                // 保存用户消息
                conversationService.saveMessage(
                        conversationId,
                        MessageRole.USER,
                        message
                );

                conversationService.updateTitleIfNeeded(
                        conversationId,
                        message
                );

                // PostgreSQL现在是历史记录来源
                var history =
                        conversationService
                                .getChatHistory(
                                        conversationId
                                );

                /*
                 * AI是流式返回的，
                 * 但数据库最终需要保存完整回答。
                 */
                StringBuilder fullReply =
                        new StringBuilder();

                qwenClient.streamChat(
                        history,
                        chunk -> {

                            fullReply.append(chunk);

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

                // Qwen全部生成完成以后再保存
                conversationService.saveMessage(
                        conversationId,
                        MessageRole.ASSISTANT,
                        fullReply.toString()
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