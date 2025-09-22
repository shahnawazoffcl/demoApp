package com.admin.school.services;

import com.admin.school.dto.message.ConversationRequestDTO;
import com.admin.school.dto.message.ConversationResponseDTO;
import com.admin.school.dto.message.MessageRequestDTO;
import com.admin.school.dto.message.MessageResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface MessagingService {
    
    // Conversation methods
    ConversationResponseDTO createConversation(ConversationRequestDTO requestDTO);
    ConversationResponseDTO getOrCreateDirectConversation(String user1Id, String user2Id);
    List<ConversationResponseDTO> getUserConversations(String userId);
    ConversationResponseDTO getConversationById(String conversationId, String userId);
    void deleteConversation(String conversationId, String userId);
    
    // Message methods
    MessageResponseDTO sendMessage(MessageRequestDTO requestDTO);
    List<MessageResponseDTO> getConversationMessages(String conversationId, String userId, Pageable pageable);
    MessageResponseDTO getMessageById(String messageId, String userId);
    MessageResponseDTO editMessage(String messageId, String newContent, String userId);
    void deleteMessage(String messageId, String userId);
    
    // Message status methods
    void markMessageAsRead(String messageId, String userId);
    void markConversationAsRead(String conversationId, String userId);
    long getUnreadMessageCount(String userId);
    long getUnreadMessageCountForConversation(String conversationId, String userId);
    
    // Search methods
    List<MessageResponseDTO> searchMessages(String userId, String query, Pageable pageable);
    List<ConversationResponseDTO> searchConversations(String userId, String query);
}
