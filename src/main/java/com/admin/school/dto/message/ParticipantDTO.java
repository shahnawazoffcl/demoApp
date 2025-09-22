package com.admin.school.dto.message;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ParticipantDTO {
    private UUID id;
    private String username;
    private String profilePictureUrl;
    private boolean isOnline;
}
