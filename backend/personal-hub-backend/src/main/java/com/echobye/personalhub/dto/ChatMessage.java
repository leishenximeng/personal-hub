package com.echobye.personalhub.dto;

public record ChatMessage(
        String role,
        String content
) {
}