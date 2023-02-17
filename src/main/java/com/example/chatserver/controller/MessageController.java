package com.example.chatserver.controller;

import com.example.chatserver.dto.MessageDTO;
import com.example.chatserver.dto.MessageType;
import com.example.chatserver.service.RedisPubService;
import com.example.chatserver.service.RedisSubService;
import com.example.chatserver.service.WebSocketPubService;
import lombok.RequiredArgsConstructor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.PostConstruct;
import java.util.*;

@RestController
@RequiredArgsConstructor
public class MessageController {
    private final RedisMessageListenerContainer redisContainer;
    private final RedisPubService redisPubService;
    private final RedisSubService redisSubService;
    private final WebSocketPubService webSocketPubService;

    private Map<String, ChannelTopic> channels;
    private List<String> roomIdList;

    @PostConstruct
    public void init() {
        channels = new HashMap<>();
        roomIdList = new ArrayList<>();
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
        if (messageDTO.getType() == MessageType.SEND) {
            redisPubService.sendRedisMessage(messageDTO.getRoomId(), messageDTO);
        }
        else if (messageDTO.getType() == MessageType.TYPING || messageDTO.getType() == MessageType.TYPED) {
            redisPubService.sendRedisMessage("typing/" + messageDTO.getRoomId(), messageDTO);
        }
        else if (messageDTO.getType() == MessageType.INVITE) {
            webSocketPubService.sendWebSocketMessage("/topic/" + messageDTO.getContent(), messageDTO);
        }
    }

    // roomId를 생성해서 전송
    @MessageMapping("/getRoomId")
    public void sendRoomId(String userIds) throws ParseException {
        Object obj = new JSONParser().parse(userIds);
        JSONObject jsonObj = (JSONObject) obj;

        String senderId = jsonObj.get("senderId").toString();
        String receiverId = jsonObj.get("roomId").toString();
        String roomId = getRoomId();
        MessageDTO messageDTO = new MessageDTO(MessageType.INVITE, senderId, roomId, "");

        System.out.println("roomId 전송: " + roomId + ", senderId: " + senderId + ", receiverId: " + receiverId);
        webSocketPubService.sendWebSocketMessage("/topic/" + senderId, messageDTO);
        webSocketPubService.sendWebSocketMessage("/topic/" + receiverId, messageDTO);
    }

    // unique한 roomId 생성
    private String getRoomId() {
        String roomId = new Random().ints(48, 123) // 48: 0의 ascii code, 123: z의 ascii code인 122에 + 1
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))  // 숫자나 알파벳이 아닌 것을 필터링함
                .limit(6)  // 길이를 6으로
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        while (roomIdList.contains(roomId)) {
            roomId = new Random().ints(48, 123)
                    .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                    .limit(10)
                    .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                    .toString();
        }
        roomIdList.add(roomId);
        return roomId;
    }
}
