package com.admin.school.controllers.utils;

import com.admin.school.dto.notification.NotificationResponseDTO;
import com.admin.school.models.Notification;

import java.util.List;

public class NotificationControllerUtils {

    public static List<NotificationResponseDTO> getNotificationsDTO(List<Notification> notificationsByUserId) {
        return notificationsByUserId.stream().map(notification -> {
            NotificationResponseDTO notificationResponseDTO = new NotificationResponseDTO();
            notificationResponseDTO.setId(notification.getId());
            notificationResponseDTO.setTitle(notification.getTitle());
            notificationResponseDTO.setContent(notification.getContent());
            notificationResponseDTO.setType(notification.getType());
            notificationResponseDTO.setReadStatus(notification.isReadStatus());
            notificationResponseDTO.setRecipientName(notification.getRecipient().getUsername());
            notificationResponseDTO.setSenderName(notification.getSender().getUsername());
            // Set post ID if the notification has a related post
            if (notification.getPost() != null) {
                notificationResponseDTO.setPostId(notification.getPost().getId().toString());
            }
            return notificationResponseDTO;
        }).toList();
    }
}
