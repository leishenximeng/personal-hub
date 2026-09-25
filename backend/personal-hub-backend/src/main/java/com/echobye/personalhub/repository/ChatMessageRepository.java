package com.echobye.personalhub.repository;

import com.echobye.personalhub.entity.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository
        extends JpaRepository<ChatMessageEntity, Long> {

    List<ChatMessageEntity>
    findByConversationIdOrderByCreatedAtAsc(Long conversationId);
}