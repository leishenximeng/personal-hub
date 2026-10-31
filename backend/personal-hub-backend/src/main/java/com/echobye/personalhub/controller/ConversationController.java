package com.echobye.personalhub.controller;

import com.echobye.personalhub.dto.ConversationResponse;
import com.echobye.personalhub.dto.CreateConversationRequest;
import com.echobye.personalhub.dto.MessageResponse;
import com.echobye.personalhub.entity.ChatMessageEntity;
import com.echobye.personalhub.entity.Conversation;
import com.echobye.personalhub.service.ConversationService;
import org.springframework.web.bind.annotation.*;
import com.echobye.personalhub.dto.UpdateConversationTitleRequest;

import java.util.List;

@RestController
@RequestMapping("/api/conversations")
public class ConversationController {

    private final ConversationService conversationService;

    public ConversationController(
            ConversationService conversationService
    ) {
        this.conversationService = conversationService;
    }

    /**
     * 创建新对话
     */
    @PostMapping
    public ConversationResponse createConversation(
            @RequestBody CreateConversationRequest request
    ) {

        Conversation conversation =
                conversationService.createConversation(
                        request.title()
                );

        return toResponse(conversation);
    }

    /**
     * 查询全部对话
     */
    @GetMapping
    public List<ConversationResponse> getAllConversations() {

        return conversationService
                .getAllConversations()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * 查询某场对话全部消息
     */
    @GetMapping("/{conversationId}/messages")
    public List<MessageResponse> getMessages(
            @PathVariable Long conversationId
    ) {

        return conversationService
                .getMessages(conversationId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private ConversationResponse toResponse(
            Conversation conversation
    ) {

        return new ConversationResponse(
                conversation.getId(),
                conversation.getTitle(),
                conversation.getCreatedAt(),
                conversation.getUpdatedAt()
        );
    }

    private MessageResponse toResponse(
            ChatMessageEntity message
    ) {

        return new MessageResponse(
                message.getId(),
                message.getRole().name(),
                message.getContent(),
                message.getCreatedAt()
        );
    }

    @PatchMapping("/{conversationId}")
    public ConversationResponse updateTitle(
            @PathVariable Long conversationId,
            @RequestBody UpdateConversationTitleRequest request
    ) {

        Conversation conversation =
                conversationService.updateTitle(
                        conversationId,
                        request.title()
                );

        return toResponse(conversation);
    }
}