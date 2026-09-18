package com.echobye.personalhub.controller;

import com.echobye.personalhub.dto.ChatRequest;
import com.echobye.personalhub.dto.ChatResponse;
import com.echobye.personalhub.service.ChatService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(ChatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping
    public ChatResponse chat(@RequestBody ChatRequest request) {

        String reply = chatService.chat(request.message());

        return new ChatResponse(reply);
    }
}