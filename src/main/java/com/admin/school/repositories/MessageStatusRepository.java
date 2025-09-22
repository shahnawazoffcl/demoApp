package com.admin.school.repositories;

import com.admin.school.models.Message;
import com.admin.school.models.MessageStatus;
import com.admin.school.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageStatusRepository extends JpaRepository<MessageStatus, UUID> {
    
    List<MessageStatus> findByMessage(Message message);
    
    Optional<MessageStatus> findByMessageAndUser(Message message, User user);
    
    @Query("SELECT ms FROM MessageStatus ms WHERE ms.message.conversation.id = :conversationId AND ms.user = :user AND ms.status = 'READ'")
    List<MessageStatus> findReadMessagesByConversationAndUser(@Param("conversationId") UUID conversationId, @Param("user") User user);
    
    @Query("SELECT COUNT(ms) FROM MessageStatus ms WHERE ms.message.conversation.id = :conversationId AND ms.user = :user AND ms.status = 'READ'")
    long countReadMessagesByConversationAndUser(@Param("conversationId") UUID conversationId, @Param("user") User user);
    
    @Query("SELECT COUNT(ms) FROM MessageStatus ms WHERE ms.message.conversation.id = :conversationId AND ms.user = :user AND ms.status != 'READ'")
    long countUnreadMessagesByConversationAndUser(@Param("conversationId") UUID conversationId, @Param("user") User user);
}
