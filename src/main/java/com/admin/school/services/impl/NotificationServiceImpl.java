package com.admin.school.services.impl;

import com.admin.school.dto.notification.NotificationResponseDTO;
import com.admin.school.models.*;
import com.admin.school.repository.NotificationRepository;
import com.admin.school.repository.UserRepository;
import com.admin.school.services.NotificationService;
import com.admin.school.utils.Constants;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public NotificationServiceImpl(NotificationRepository notificationRepository, UserRepository userRepository) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }


    @Override
    public List<Notification> getNotificationsByUserId(String userId) {
        Optional<User> optionalUser = userRepository.findById(UUID.fromString(userId));
        if (optionalUser.isPresent()) {
            List<Notification> notifications = notificationRepository.findByRecipient(optionalUser.get());
            return notifications;
        }
        return null;
    }

    @Override
    public void sendPostLikeNotification(Post post, User user) {
        Notification notification = new Notification();
        notification.setTitle("liked your post");
        notification.setContent(user.getUsername() + Constants.LikedByUser);
        notification.setRecipient(post.getUser());
        notification.setSender(user);
        notification.setPost(post);
        notification.setType(Constants.NotificationTypeLike);
        notificationRepository.save(notification);
    }

    @Override
    public void deletePostLikeNotification(Post post, User user) {
        notificationRepository.deleteNotificationsByTypeAndRecipientAndSenderAndPost(Constants.NotificationTypeLike,post.getUser(),user,post);
    }

    @Override
    public void markNotificationAsRead(String notificationId) {
        Optional<Notification> optionalNotification = notificationRepository.findById(UUID.fromString(notificationId));
        if (optionalNotification.isPresent()) {
            Notification notification = optionalNotification.get();
            notification.setReadStatus(true);
            notificationRepository.save(notification);
        }
    }

    @Override
    public void markAllNotificationsAsRead(String userId) {
        Optional<User> optionalUser = userRepository.findById(UUID.fromString(userId));
        if (optionalUser.isPresent()) {
            List<Notification> notifications = notificationRepository.findByRecipient(optionalUser.get());
            for (Notification notification : notifications) {
                notification.setReadStatus(true);
            }
            notificationRepository.saveAll(notifications);
        }
    }

    @Override
    public void sendConnectionRequestNotification(User user1, User user2) {
        Notification notification = new Notification();
        notification.setTitle("Connection Request");
        notification.setContent(user1.getUsername() + Constants.ConnectionsRequest);
        notification.setRecipient(user2);
        notification.setSender(user1);
        notification.setType(Constants.NotificationTypeConnectionRequest);
        notificationRepository.save(notification);
    }

    @Override
    public Notification getNotificationById(String notificationId) {
        Optional<Notification> optionalNotification = notificationRepository.findById(UUID.fromString(notificationId));
        return optionalNotification.orElse(null);
    }

    @Override
    public void deleteNotification(String notificationId) {
        Optional<Notification> optionalNotification = notificationRepository.findById(UUID.fromString(notificationId));
        optionalNotification.ifPresent(notificationRepository::delete);
    }

    @Override
    public void sendCommentNotification(Comment savedComment, BaseModel baseModel) {
        Notification notification = new Notification();
        notification.setTitle(savedComment.getAuthor().getUsername() + Constants.CommentedOnYourPost);
        notification.setContent(savedComment.getAuthor().getUsername()+": "+savedComment.getContent());
        notification.setRecipient(savedComment.getPost().getUser());
        notification.setSender(savedComment.getAuthor());
        notification.setPost(savedComment.getPost());
        notification.setComment(savedComment);
        notification.setType(Constants.NotificationTypeComment);
        notificationRepository.save(notification);
    }
}
