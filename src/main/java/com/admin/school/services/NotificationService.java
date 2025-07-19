package com.admin.school.services;

import com.admin.school.models.*;

import java.util.List;

public interface NotificationService {
    List<Notification> getNotificationsByUserId(String userId);

    void sendPostLikeNotification(Post post, User user);

    void deletePostLikeNotification(Post post, User user);
    
    void markNotificationAsRead(String notificationId);
    
    void markAllNotificationsAsRead(String userId);

    void sendConnectionRequestNotification(User user1, User user2);

    Notification getNotificationById(String notificationId);

    void deleteNotification(String notificationId);

    void sendCommentNotification(Comment savedComment, BaseModel baseModel);
}
