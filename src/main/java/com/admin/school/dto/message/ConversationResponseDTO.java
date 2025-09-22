package com.admin.school.dto.message;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ConversationResponseDTO {
    private UUID id;
    private String title;
    private boolean isGroup;
    private List<ParticipantDTO> participants;
    private MessageResponseDTO lastMessage;
    private Date lastActivityAt;
    private long unreadCount;
    private Date createdAt;
}

