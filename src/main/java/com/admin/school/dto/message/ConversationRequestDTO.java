package com.admin.school.dto.message;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ConversationRequestDTO {
    private List<UUID> participantIds;
    private String title; // For group conversations
    private boolean isGroup = false;
}
