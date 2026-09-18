package com.echobye.personalhub.controller;

import com.echobye.personalhub.dto.ChatRequest;
import com.echobye.personalhub.dto.ChatResponse;
import com.echobye.personalhub.service.ChatService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    /**
     * 普通聊天
     */
    @PostMapping
    public ChatResponse chat(
            @RequestBody ChatRequest request
    ) {

        String reply =
                chatService.chat(request.message());

        return new ChatResponse(reply);
    }

    /**
     * 流式聊天
     */
    @PostMapping(
            value = "/stream",
            produces = MediaType.TEXT_EVENT_STREAM_VALUE
    )
    public SseEmitter streamChat(
            @RequestBody ChatRequest request
    ) {

        return chatService.streamChat(
                request.message()
        );
    }
}