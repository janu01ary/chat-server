package com.example.chatserver.controller;

import com.example.chatserver.dto.MessageDTO;
import com.example.chatserver.service.RedisPubService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MessageController {
    private final RedisPubService redisPubService;
    private final SimpMessagingTemplate messagingTemplate;


    @MessageMapping("/receive")
    public void sendMessage(MessageDTO messageDTO) {
        System.out.println("socket에서 받음 : " + messageDTO.getSenderId() + " " + messageDTO.getType());
        redisPubService.sendRedisMessage(messageDTO.getSenderId(), messageDTO);
        messagingTemplate.convertAndSend("/topic/" + messageDTO.getSenderId(), messageDTO);
    }
}
