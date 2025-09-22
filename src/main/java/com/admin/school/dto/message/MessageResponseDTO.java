package com.admin.school.dto.message;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class MessageResponseDTO {
    private UUID id;
    private String content;
    private String messageType;
    private String mediaUrl;
    private UUID conversationId;
    private UUID senderId;
    private String senderName;
    private String senderProfilePicture;
    private UUID replyToMessageId;
    private String replyToContent;
    private boolean isEdited;
    private Date editedAt;
    private Date createdAt;
    private String status; // SENT, DELIVERED, READ
    private Date readAt;
}
