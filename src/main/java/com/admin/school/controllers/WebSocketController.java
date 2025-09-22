package com.admin.school.controllers;

import com.admin.school.dto.message.WebSocketMessageDTO;
import com.admin.school.services.AuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;

@Controller
@Slf4j
public class WebSocketController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private AuthService authService;

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/public")
    public WebSocketMessageDTO sendMessage(@Payload WebSocketMessageDTO webSocketMessage) {
        log.info("Received message: {}", webSocketMessage.getContent());
        return webSocketMessage;
    }

    @MessageMapping("/chat.addUser")
    @SendTo("/topic/public")
    public WebSocketMessageDTO addUser(@Payload WebSocketMessageDTO webSocketMessage,
                                       SimpMessageHeaderAccessor headerAccessor) {
        // Add username in web socket session
        headerAccessor.getSessionAttributes().put("username", webSocketMessage.getSenderName());
        headerAccessor.getSessionAttributes().put("userId", webSocketMessage.getSenderId());
        return webSocketMessage;
    }

    @MessageMapping("/conversation/{conversationId}/send")
    public void sendMessageToConversation(@DestinationVariable String conversationId,
                                         @Payload WebSocketMessageDTO message,
                                         SimpMessageHeaderAccessor headerAccessor) {
        log.info("Sending message to conversation {}: {}", conversationId, message.getContent());
        
        // Set timestamp
        message.setTimestamp(java.time.Instant.now().toString());
        
        // Send to all subscribers of this conversation
        messagingTemplate.convertAndSend("/topic/conversation/" + conversationId, message);
    }

    @MessageMapping("/conversation/{conversationId}/typing")
    public void handleTyping(@DestinationVariable String conversationId,
                            @Payload WebSocketMessageDTO message) {
        log.info("User {} is typing in conversation {}", message.getUserId(), conversationId);
        
        // Send typing indicator to all other participants
        messagingTemplate.convertAndSend("/topic/conversation/" + conversationId + "/typing", message);
    }

    @MessageMapping("/conversation/{conversationId}/read")
    public void handleReadReceipt(@DestinationVariable String conversationId,
                                 @Payload WebSocketMessageDTO message) {
        log.info("User {} read message {} in conversation {}", 
                message.getUserId(), message.getMessageId(), conversationId);
        
        // Send read receipt to all participants
        messagingTemplate.convertAndSend("/topic/conversation/" + conversationId + "/read", message);
    }

    @MessageMapping("/user/{userId}/notify")
    public void sendNotificationToUser(@DestinationVariable String userId,
                                      @Payload WebSocketMessageDTO message) {
        log.info("Sending notification to user {}: {}", userId, message.getContent());
        
        // Send notification to specific user
        messagingTemplate.convertAndSendToUser(userId, "/queue/notifications", message);
    }
}
