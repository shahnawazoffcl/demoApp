package com.admin.school.controllers;

import com.admin.school.dto.message.ConversationRequestDTO;
import com.admin.school.dto.message.ConversationResponseDTO;
import com.admin.school.dto.message.MessageRequestDTO;
import com.admin.school.dto.message.MessageResponseDTO;
import com.admin.school.services.AuthService;
import com.admin.school.services.MessagingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/messaging")
@Slf4j
public class MessagingController {
    
    private final MessagingService messagingService;
    private final AuthService authService;
    
    public MessagingController(MessagingService messagingService, AuthService authService) {
        this.messagingService = messagingService;
        this.authService = authService;
    }
    
    // Conversation endpoints
    @PostMapping("/conversations")
    public ResponseEntity<ConversationResponseDTO> createConversation(
            @RequestHeader("token") String token,
            @RequestBody ConversationRequestDTO requestDTO) {
        try {
            String userId = authService.getUserIdFromToken(token);
            authService.validateUser(token, userId);
            
            ConversationResponseDTO conversation = messagingService.createConversation(requestDTO);
            return ResponseEntity.ok(conversation);
        } catch (Exception e) {
            log.error("Error creating conversation: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/conversations/{userId}")
    public ResponseEntity<List<ConversationResponseDTO>> getUserConversations(
            @RequestHeader("token") String token,
            @PathVariable String userId) {
        try {
            authService.validateUser(token, userId);
            
            List<ConversationResponseDTO> conversations = messagingService.getUserConversations(userId);
            return ResponseEntity.ok(conversations);
        } catch (Exception e) {
            log.error("Error getting user conversations: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/conversations/{conversationId}/user/{userId}")
    public ResponseEntity<ConversationResponseDTO> getConversation(
            @RequestHeader("token") String token,
            @PathVariable String conversationId,
            @PathVariable String userId) {
        try {
            authService.validateUser(token, userId);
            
            ConversationResponseDTO conversation = messagingService.getConversationById(conversationId, userId);
            return ResponseEntity.ok(conversation);
        } catch (Exception e) {
            log.error("Error getting conversation: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PostMapping("/conversations/direct")
    public ResponseEntity<ConversationResponseDTO> getOrCreateDirectConversation(
            @RequestHeader("token") String token,
            @RequestParam String user1Id,
            @RequestParam String user2Id) {
        try {
            authService.validateUser(token, user1Id);
            
            ConversationResponseDTO conversation = messagingService.getOrCreateDirectConversation(user1Id, user2Id);
            return ResponseEntity.ok(conversation);
        } catch (Exception e) {
            log.error("Error getting/creating direct conversation: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    // Message endpoints
    @PostMapping("/messages")
    public ResponseEntity<MessageResponseDTO> sendMessage(
            @RequestHeader("token") String token,
            @RequestBody MessageRequestDTO requestDTO) {
        try {
            authService.validateUser(token, requestDTO.getSenderId());
            
            MessageResponseDTO message = messagingService.sendMessage(requestDTO);
            return ResponseEntity.ok(message);
        } catch (Exception e) {
            log.error("Error sending message: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<List<MessageResponseDTO>> getConversationMessages(
            @RequestHeader("token") String token,
            @PathVariable String conversationId,
            @RequestParam String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size) {
        try {
            authService.validateUser(token, userId);
            
            Pageable pageable = PageRequest.of(page, size);
            List<MessageResponseDTO> messages = messagingService.getConversationMessages(conversationId, userId, pageable);
            return ResponseEntity.ok(messages);
        } catch (Exception e) {
            log.error("Error getting conversation messages: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/messages/{messageId}/read")
    public ResponseEntity<String> markMessageAsRead(
            @RequestHeader("token") String token,
            @PathVariable String messageId,
            @RequestParam String userId) {
        try {
            authService.validateUser(token, userId);
            
            messagingService.markMessageAsRead(messageId, userId);
            return ResponseEntity.ok("Message marked as read");
        } catch (Exception e) {
            log.error("Error marking message as read: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @PutMapping("/conversations/{conversationId}/read")
    public ResponseEntity<String> markConversationAsRead(
            @RequestHeader("token") String token,
            @PathVariable String conversationId,
            @RequestParam String userId) {
        try {
            authService.validateUser(token, userId);
            
            messagingService.markConversationAsRead(conversationId, userId);
            return ResponseEntity.ok("Conversation marked as read");
        } catch (Exception e) {
            log.error("Error marking conversation as read: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/unread-count/{userId}")
    public ResponseEntity<Long> getUnreadMessageCount(
            @RequestHeader("token") String token,
            @PathVariable String userId) {
        try {
            authService.validateUser(token, userId);
            
            long unreadCount = messagingService.getUnreadMessageCount(userId);
            return ResponseEntity.ok(unreadCount);
        } catch (Exception e) {
            log.error("Error getting unread message count: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
    
    @GetMapping("/conversations/{conversationId}/unread-count/{userId}")
    public ResponseEntity<Long> getUnreadMessageCountForConversation(
            @RequestHeader("token") String token,
            @PathVariable String conversationId,
            @PathVariable String userId) {
        try {
            authService.validateUser(token, userId);
            
            long unreadCount = messagingService.getUnreadMessageCountForConversation(conversationId, userId);
            return ResponseEntity.ok(unreadCount);
        } catch (Exception e) {
            log.error("Error getting unread message count for conversation: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
