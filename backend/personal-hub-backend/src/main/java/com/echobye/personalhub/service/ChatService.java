package com.echobye.personalhub.service;

import com.echobye.personalhub.client.QwenClient;
import org.springframework.stereotype.Service;

@Service
public class ChatService {

    private final QwenClient qwenClient;

    public ChatService(QwenClient qwenClient) {
        this.qwenClient = qwenClient;
    }

    public String chat(String message) {
        return qwenClient.chat(message);
    }
}