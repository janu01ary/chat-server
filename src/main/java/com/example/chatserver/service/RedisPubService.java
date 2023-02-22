package com.example.chatserver.service;

import com.example.chatserver.dto.MessageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RedisPubService {

    private final RedisTemplate redisTemplate;

    public void sendRedisMessage(String channel, MessageDTO messageDTO) {
        redisTemplate.convertAndSend(channel, messageDTO);
    }

}
