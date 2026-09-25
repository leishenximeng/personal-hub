package com.echobye.personalhub.dto;

public record ChatRequest(
        Long conversationId,
        String message
) {
}