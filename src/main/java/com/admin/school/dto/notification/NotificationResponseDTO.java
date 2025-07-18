package com.admin.school.dto.notification;


import lombok.Getter;
import lombok.Setter;
import java.util.UUID;

@Setter
@Getter
public class NotificationResponseDTO {
    private UUID id;
    private String title;
    private String content;
    private String type;
    private boolean readStatus;
    private String recipientName;
    private String senderName;
    private String postId; // Add post ID for navigation
}
