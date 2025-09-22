package com.admin.school.dto.message;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class MessageRequestDTO {
    private String content;
    private String messageType = "TEXT";
    private String mediaUrl;
    private UUID conversationId;
    private UUID replyToMessageId;
    private String senderId;
}
