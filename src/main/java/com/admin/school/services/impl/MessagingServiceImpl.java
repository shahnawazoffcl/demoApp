package com.admin.school.services.impl;

import com.admin.school.dto.message.ConversationRequestDTO;
import com.admin.school.dto.message.ConversationResponseDTO;
import com.admin.school.dto.message.MessageRequestDTO;
import com.admin.school.dto.message.MessageResponseDTO;
import com.admin.school.dto.message.ParticipantDTO;
import com.admin.school.models.*;
import com.admin.school.repositories.ConversationRepository;
import com.admin.school.repositories.MessageRepository;
import com.admin.school.repositories.MessageStatusRepository;
import com.admin.school.repository.UserRepository;
import com.admin.school.services.MessagingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class MessagingServiceImpl implements MessagingService {
    
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final MessageStatusRepository messageStatusRepository;
    private final UserRepository userRepository;
    private final SimpMessagingTemplate messagingTemplate;
    
    public MessagingServiceImpl(ConversationRepository conversationRepository,
                               MessageRepository messageRepository,
                               MessageStatusRepository messageStatusRepository,
                               UserRepository userRepository,
                               SimpMessagingTemplate messagingTemplate) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.messageStatusRepository = messageStatusRepository;
        this.userRepository = userRepository;
        this.messagingTemplate = messagingTemplate;
    }
    
    @Override
    @Transactional
    public ConversationResponseDTO createConversation(ConversationRequestDTO requestDTO) {
        log.info("Creating conversation with participants: {}", requestDTO.getParticipantIds());
        
        List<User> participants = userRepository.findAllById(requestDTO.getParticipantIds());
        if (participants.size() != requestDTO.getParticipantIds().size()) {
            throw new RuntimeException("Some participants not found");
        }
        
        Conversation conversation = new Conversation();
        conversation.setParticipants(participants);
        conversation.setTitle(requestDTO.getTitle());
        conversation.setGroup(requestDTO.isGroup());
        conversation.setLastActivityAt(new Date());
        
        conversation = conversationRepository.save(conversation);
        
        return mapConversationToDTO(conversation, null);
    }
    
    @Override
    @Transactional
    public ConversationResponseDTO getOrCreateDirectConversation(String user1Id, String user2Id) {
        log.info("Getting or creating direct conversation between users: {} and {}", user1Id, user2Id);
        
        User user1 = userRepository.findById(UUID.fromString(user1Id))
                .orElseThrow(() -> new RuntimeException("User not found: " + user1Id));
        User user2 = userRepository.findById(UUID.fromString(user2Id))
                .orElseThrow(() -> new RuntimeException("User not found: " + user2Id));
        
        Optional<Conversation> existingConversation = conversationRepository
                .findDirectConversationBetweenUsers(user1, user2);
        
        if (existingConversation.isPresent()) {
            return mapConversationToDTO(existingConversation.get(), user1Id);
        }
        
        // Create new direct conversation
        Conversation conversation = new Conversation();
        conversation.setParticipants(Arrays.asList(user1, user2));
        conversation.setGroup(false);
        conversation.setLastActivityAt(new Date());
        
        conversation = conversationRepository.save(conversation);
        
        return mapConversationToDTO(conversation, user1Id);
    }
    
    @Override
    public List<ConversationResponseDTO> getUserConversations(String userId) {
        log.info("Getting conversations for user: {}", userId);
        
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        
        List<Conversation> conversations = conversationRepository
                .findByParticipantOrderByLastActivityDesc(user);
        
        return conversations.stream()
                .map(conv -> mapConversationToDTO(conv, userId))
                .collect(Collectors.toList());
    }
    
    @Override
    public ConversationResponseDTO getConversationById(String conversationId, String userId) {
        log.info("Getting conversation: {} for user: {}", conversationId, userId);
        
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        
        Conversation conversation = conversationRepository
                .findByParticipantAndId(user, UUID.fromString(conversationId))
                .orElseThrow(() -> new RuntimeException("Conversation not found or access denied"));
        
        return mapConversationToDTO(conversation, userId);
    }
    
    @Override
    @Transactional
    public MessageResponseDTO sendMessage(MessageRequestDTO requestDTO) {
        log.info("Sending message to conversation: {}", requestDTO.getConversationId());
        
        User sender = userRepository.findById(UUID.fromString(requestDTO.getSenderId()))
                .orElseThrow(() -> new RuntimeException("Sender not found: " + requestDTO.getSenderId()));
        
        Conversation conversation = conversationRepository.findById(requestDTO.getConversationId())
                .orElseThrow(() -> new RuntimeException("Conversation not found"));
        
        // Check if sender is participant
        boolean isParticipant = conversation.getParticipants().stream()
                .anyMatch(p -> p.getId().equals(sender.getId()));
        if (!isParticipant) {
            throw new RuntimeException("User is not a participant in this conversation");
        }
        
        Message message = new Message();
        message.setSender(sender);
        message.setConversation(conversation);
        message.setContent(requestDTO.getContent());
        message.setMessageType(Message.MessageType.valueOf(requestDTO.getMessageType()));
        message.setMediaUrl(requestDTO.getMediaUrl());
        
        if (requestDTO.getReplyToMessageId() != null) {
            Message replyTo = messageRepository.findById(requestDTO.getReplyToMessageId())
                    .orElseThrow(() -> new RuntimeException("Reply-to message not found"));
            message.setReplyTo(replyTo);
        }
        
        message = messageRepository.save(message);
        
        // Create message statuses for all participants
        for (User participant : conversation.getParticipants()) {
            MessageStatus status = new MessageStatus();
            status.setMessage(message);
            status.setUser(participant);
            status.setStatus(participant.getId().equals(sender.getId()) ? 
                    MessageStatus.Status.SENT : MessageStatus.Status.DELIVERED);
            messageStatusRepository.save(status);
        }
        
        // Update conversation last activity
        conversation.setLastActivityAt(new Date());
        conversation.setLastMessage(message);
        conversationRepository.save(conversation);
        
        // Broadcast message via WebSocket to all participants
        MessageResponseDTO messageDTO = mapMessageToDTO(message, sender.getId().toString());
        messagingTemplate.convertAndSend("/topic/conversation/" + conversation.getId(), messageDTO);
        
        return messageDTO;
    }
    
    @Override
    public List<MessageResponseDTO> getConversationMessages(String conversationId, String userId, Pageable pageable) {
        log.info("Getting messages for conversation: {} and user: {}", conversationId, userId);
        
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        
        Conversation conversation = conversationRepository
                .findByParticipantAndId(user, UUID.fromString(conversationId))
                .orElseThrow(() -> new RuntimeException("Conversation not found or access denied"));
        
        Page<Message> messages = messageRepository
                .findByConversationOrderByCreatedAtDesc(conversation, pageable);
        
        return messages.getContent().stream()
                .map(msg -> mapMessageToDTO(msg, userId))
                .collect(Collectors.toList());
    }
    
    @Override
    @Transactional
    public void markMessageAsRead(String messageId, String userId) {
        log.info("Marking message: {} as read for user: {}", messageId, userId);
        
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        
        Message message = messageRepository.findById(UUID.fromString(messageId))
                .orElseThrow(() -> new RuntimeException("Message not found"));
        
        MessageStatus status = messageStatusRepository.findByMessageAndUser(message, user)
                .orElseThrow(() -> new RuntimeException("Message status not found"));
        
        status.setStatus(MessageStatus.Status.READ);
        status.setReadAt(new Date());
        messageStatusRepository.save(status);
    }
    
    @Override
    @Transactional
    public void markConversationAsRead(String conversationId, String userId) {
        log.info("Marking all messages in conversation: {} as read for user: {}", conversationId, userId);
        
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        
        Conversation conversation = conversationRepository
                .findByParticipantAndId(user, UUID.fromString(conversationId))
                .orElseThrow(() -> new RuntimeException("Conversation not found or access denied"));
        
        List<Message> unreadMessages = messageRepository.findByConversationOrderByCreatedAtAsc(conversation)
                .stream()
                .filter(msg -> !msg.getSender().getId().equals(user.getId()))
                .collect(Collectors.toList());
        
        for (Message message : unreadMessages) {
            MessageStatus status = messageStatusRepository.findByMessageAndUser(message, user)
                    .orElse(null);
            if (status != null && status.getStatus() != MessageStatus.Status.READ) {
                status.setStatus(MessageStatus.Status.READ);
                status.setReadAt(new Date());
                messageStatusRepository.save(status);
            }
        }
    }
    
    @Override
    public long getUnreadMessageCount(String userId) {
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        
        List<Conversation> conversations = conversationRepository
                .findByParticipantOrderByLastActivityDesc(user);
        
        long totalUnread = 0;
        for (Conversation conversation : conversations) {
            totalUnread += messageStatusRepository
                    .countUnreadMessagesByConversationAndUser(conversation.getId(), user);
        }
        
        return totalUnread;
    }
    
    @Override
    public long getUnreadMessageCountForConversation(String conversationId, String userId) {
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        
        return messageStatusRepository
                .countUnreadMessagesByConversationAndUser(UUID.fromString(conversationId), user);
    }
    
    // Helper methods
    private ConversationResponseDTO mapConversationToDTO(Conversation conversation, String currentUserId) {
        ConversationResponseDTO dto = new ConversationResponseDTO();
        dto.setId(conversation.getId());
        dto.setTitle(conversation.getTitle());
        dto.setGroup(conversation.isGroup());
        dto.setLastActivityAt(conversation.getLastActivityAt());
        dto.setCreatedAt(conversation.getCreatedAt());
        
        // Map participants
        List<ParticipantDTO> participants = conversation.getParticipants().stream()
                .map(this::mapUserToParticipantDTO)
                .collect(Collectors.toList());
        dto.setParticipants(participants);
        
        // Map last message
        if (conversation.getLastMessage() != null) {
            dto.setLastMessage(mapMessageToDTO(conversation.getLastMessage(), currentUserId));
        }
        
        // Set unread count
        if (currentUserId != null) {
            dto.setUnreadCount(getUnreadMessageCountForConversation(
                    conversation.getId().toString(), currentUserId));
        }
        
        return dto;
    }
    
    private MessageResponseDTO mapMessageToDTO(Message message, String currentUserId) {
        MessageResponseDTO dto = new MessageResponseDTO();
        dto.setId(message.getId());
        dto.setContent(message.getContent());
        dto.setMessageType(message.getMessageType().toString());
        dto.setMediaUrl(message.getMediaUrl());
        dto.setConversationId(message.getConversation().getId());
        dto.setSenderId(message.getSender().getId());
        dto.setSenderName(message.getSender().getUsername());
        dto.setSenderProfilePicture(message.getSender().getProfilePictureUrl());
        dto.setEdited(message.isEdited());
        dto.setEditedAt(message.getEditedAt());
        dto.setCreatedAt(message.getCreatedAt());
        
        if (message.getReplyTo() != null) {
            dto.setReplyToMessageId(message.getReplyTo().getId());
            dto.setReplyToContent(message.getReplyTo().getContent());
        }
        
        // Get message status for current user
        if (currentUserId != null) {
            User currentUser = userRepository.findById(UUID.fromString(currentUserId)).orElse(null);
            if (currentUser != null) {
                MessageStatus status = messageStatusRepository
                        .findByMessageAndUser(message, currentUser).orElse(null);
                if (status != null) {
                    dto.setStatus(status.getStatus().toString());
                    dto.setReadAt(status.getReadAt());
                }
            }
        }
        
        return dto;
    }
    
    private ParticipantDTO mapUserToParticipantDTO(User user) {
        ParticipantDTO dto = new ParticipantDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setProfilePictureUrl(user.getProfilePictureUrl());
        dto.setOnline(false); // TODO: Implement online status
        return dto;
    }
    
    // Placeholder implementations for remaining methods
    @Override
    public void deleteConversation(String conversationId, String userId) {
        // TODO: Implement conversation deletion
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    @Override
    public MessageResponseDTO getMessageById(String messageId, String userId) {
        // TODO: Implement get message by ID
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    @Override
    public MessageResponseDTO editMessage(String messageId, String newContent, String userId) {
        // TODO: Implement message editing
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    @Override
    public void deleteMessage(String messageId, String userId) {
        // TODO: Implement message deletion
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    @Override
    public List<MessageResponseDTO> searchMessages(String userId, String query, Pageable pageable) {
        // TODO: Implement message search
        throw new UnsupportedOperationException("Not implemented yet");
    }
    
    @Override
    public List<ConversationResponseDTO> searchConversations(String userId, String query) {
        // TODO: Implement conversation search
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
