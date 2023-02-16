package com.example.chatserver.controller;

import com.example.chatserver.dto.MessageDTO;
import com.example.chatserver.service.RedisPubService;
import com.example.chatserver.service.RedisSubService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class MessageController {
    private final RedisMessageListenerContainer redisContainer;
    private final RedisPubService redisPubService;
    private final RedisSubService redisSubService;
    private final SimpMessagingTemplate messagingTemplate;

    private Map<String, ChannelTopic> channels;

    @PostConstruct
    public void init() {
        channels = new HashMap<>();
    }

    @MessageMapping("/sub")
    public void sub(String channel_name) {
        // redis 구독
        System.out.println("socket에서 받음 : " + channel_name);
        if (!channels.containsKey(channel_name)) {
            ChannelTopic channel = new ChannelTopic(channel_name);
            redisContainer.addMessageListener(redisSubService, channel);
            channels.put(channel_name, channel);
            System.out.println("redis subscribe : " + channel_name + ", " + channels.toString());
        }
    }

    @MessageMapping("/receive")
    public void sendMessage(MessageDTO messageDTO) {
        System.out.println("socket에서 받음 : " + messageDTO.getSenderId() + " " + messageDTO.getType());
        redisPubService.sendRedisMessage(messageDTO.getSenderId(), messageDTO);
        messagingTemplate.convertAndSend("/topic/" + messageDTO.getSenderId(), messageDTO);
    }
}
