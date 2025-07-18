package com.admin.school.repository;

import com.admin.school.models.Notification;
import com.admin.school.models.Post;
import com.admin.school.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;


@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findByRecipient(User user);

    void deleteNotificationsByTypeAndRecipientAndSenderAndPost(String notificationTypeLike, User user, User user1, Post post);
}
