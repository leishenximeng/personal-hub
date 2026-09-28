package com.echobye.personalhub.service;

import com.echobye.personalhub.entity.ChatMessageEntity;
import com.echobye.personalhub.entity.Conversation;
import com.echobye.personalhub.repository.ChatMessageRepository;
import com.echobye.personalhub.repository.ConversationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.echobye.personalhub.dto.ChatMessage;
import com.echobye.personalhub.entity.MessageRole;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final ChatMessageRepository chatMessageRepository;

    public ConversationService(
            ConversationRepository conversationRepository,
            ChatMessageRepository chatMessageRepository
    ) {
        this.conversationRepository = conversationRepository;
        this.chatMessageRepository = chatMessageRepository;
    }

    /**
     * 创建一场新对话
     */
    public Conversation createConversation(String title) {

        Conversation conversation = new Conversation();

        conversation.setTitle(title);

        return conversationRepository.save(conversation);
    }

    /**
     * 查询所有对话
     */
    public List<Conversation> getAllConversations() {

        return conversationRepository.findAll();
    }

    /**
     * 查询某场对话中的全部消息
     */
    @Transactional(readOnly = true)
    public List<ChatMessageEntity> getMessages(Long conversationId) {

        return chatMessageRepository
                .findByConversationIdOrderByCreatedAtAsc(
                        conversationId
                );
    }

    @Transactional
    public ChatMessageEntity saveMessage(
            Long conversationId,
            MessageRole role,
            String content
    ) {

        Conversation conversation =
                conversationRepository
                        .findById(conversationId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "对话不存在"
                                )
                        );

        ChatMessageEntity message =
                new ChatMessageEntity();

        message.setConversation(conversation);
        message.setRole(role);
        message.setContent(content);

        return chatMessageRepository.save(message);
    }

    @Transactional(readOnly = true)
    public List<ChatMessage> getChatHistory(
            Long conversationId
    ) {

        return chatMessageRepository
                .findByConversationIdOrderByCreatedAtAsc(
                        conversationId
                )
                .stream()
                .map(message ->
                        new ChatMessage(
                                message.getRole()
                                        == MessageRole.USER
                                        ? "user"
                                        : "assistant",

                                message.getContent()
                        )
                )
                .toList();
    }

    @Transactional
    public void updateTitleIfNeeded(
            Long conversationId,
            String firstMessage
    ) {

        Conversation conversation =
                conversationRepository
                        .findById(conversationId)
                        .orElseThrow(() ->
                                new ResponseStatusException(
                                        HttpStatus.NOT_FOUND,
                                        "对话不存在"
                                )
                        );

        // 只有标题仍然叫“新对话”时才修改
        if (!"新对话".equals(conversation.getTitle())) {
            return;
        }

        String title = firstMessage.trim();

        // 太长的话截取前20个字符
        if (title.length() > 20) {
            title = title.substring(0, 20) + "...";
        }

        conversation.setTitle(title);
    }
}