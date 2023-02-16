package com.example.chatserver.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class MessageDTO implements Serializable {
    private MessageType type;
    private String senderId;
    private String roomId;
    private String content;
}
