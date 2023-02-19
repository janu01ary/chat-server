package com.example.chatserver.controller;

import com.example.chatserver.dto.MessageDTO;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MessageController {

    @MessageMapping("/receive")
    public void sendMessage(MessageDTO messageDTO) {
        System.out.println("socket에서 받음 : " + messageDTO.getSenderId() + " " + messageDTO.getType());
    }
}
