package com.echobye.personalhub.dto;

import java.util.List;

public record ChatRequest(
        List<ChatMessage> messages
) {
}