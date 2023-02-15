package com.example.chatserver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class MessageDTO {
    private MessageType type;
    private String senderId;
    private String roomId;
    private String content;
}
