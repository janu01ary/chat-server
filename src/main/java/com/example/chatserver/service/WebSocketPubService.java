package com.example.chatserver.service;

import com.example.chatserver.dto.MessageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class WebSocketPubService {
    private final SimpMessagingTemplate messagingTemplate;

    public void sendWebSocketMessage(String destination, MessageDTO messageDTO) {
        messagingTemplate.convertAndSend(destination, messageDTO);
    }
}
