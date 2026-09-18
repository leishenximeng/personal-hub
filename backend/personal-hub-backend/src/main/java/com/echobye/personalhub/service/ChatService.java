package com.echobye.personalhub.service;

import com.echobye.personalhub.client.QwenClient;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

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
    public String chat(String message) {
        return qwenClient.chat(message);
    }

    /**
     * 流式聊天
     */
    public SseEmitter streamChat(String message) {

        // 5分钟超时
        SseEmitter emitter = new SseEmitter(300_000L);

        /*
         * Qwen 的网络请求会持续一段时间，
         * 所以不能堵住 Spring 当前处理请求的线程。
         *
         * Java 21 已经正式支持虚拟线程。
         */
        Thread.startVirtualThread(() -> {

            try {

                qwenClient.streamChat(message, chunk -> {

                    try {

                        // Qwen生成一段
                        // 就马上发给浏览器一段
                        emitter.send(
                                SseEmitter
                                        .event()
                                        .data(chunk)
                        );

                    } catch (IOException e) {

                        throw new RuntimeException(e);
                    }
                });

                // 告诉前端：AI输出结束
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