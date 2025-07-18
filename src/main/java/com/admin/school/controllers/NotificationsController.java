package com.admin.school.controllers;


import com.admin.school.controllers.utils.NotificationControllerUtils;
import com.admin.school.dto.notification.NotificationResponseDTO;
import com.admin.school.services.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
public class NotificationsController {

    private final NotificationService notificationService;

    public NotificationsController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/notifications/{userId}")
    public ResponseEntity<List<NotificationResponseDTO>> getNotificationsByUserId(@PathVariable("userId") String userId) {
        List<NotificationResponseDTO> notificationResponseDTOList = NotificationControllerUtils.getNotificationsDTO(notificationService.getNotificationsByUserId(userId));
        if (notificationResponseDTOList.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(notificationResponseDTOList);
    }

    @PutMapping("/{notificationId}/read")
    public ResponseEntity<String> markNotificationAsRead(@PathVariable("notificationId") String notificationId) {
        notificationService.markNotificationAsRead(notificationId);
        return ResponseEntity.ok("Notification marked as read");
    }

    @PutMapping("/{userId}/read-all")
    public ResponseEntity<String> markAllNotificationsAsRead(@PathVariable("userId") String userId) {
        notificationService.markAllNotificationsAsRead(userId);
        return ResponseEntity.ok("All notifications marked as read");
    }
}
