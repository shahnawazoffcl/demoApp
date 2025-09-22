package com.admin.school.dto.message;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class WebSocketMessageDTO {
    private String type; // MESSAGE, TYPING, READ_RECEIPT, etc.
    private UUID conversationId;
    private UUID messageId;
    private String content;
    private UUID senderId;
    private String senderName;
    private String timestamp;
    private String status; // SENT, DELIVERED, READ
    private boolean isTyping;
    private UUID userId; // For typing indicators and read receipts
}
