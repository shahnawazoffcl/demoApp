package com.admin.school.services;

import com.admin.school.models.Notification;
import com.admin.school.models.Post;
import com.admin.school.models.User;

import java.util.List;

public interface NotificationService {
    List<Notification> getNotificationsByUserId(String userId);

    void sendPostLikeNotification(Post post, User user);

    void deletePostLikeNotification(Post post, User user);
    
    void markNotificationAsRead(String notificationId);
    
    void markAllNotificationsAsRead(String userId);
}
