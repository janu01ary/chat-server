package com.example.chatserver.service;

import com.example.chatserver.dto.MessageDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisSubService implements MessageListener {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void onMessage(Message message, byte[] pattern) {
        try {
            MessageDTO messageDTO = mapper.readValue(new String(message.getBody()), MessageDTO.class);
            System.out.println("redis에서 받음 : " + messageDTO.getType() + ", " + messageDTO.getSenderId());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
