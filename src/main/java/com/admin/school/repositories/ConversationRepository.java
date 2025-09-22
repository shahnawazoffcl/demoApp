package com.admin.school.repositories;

import com.admin.school.models.Conversation;
import com.admin.school.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ConversationRepository extends JpaRepository<Conversation, UUID> {
    
    @Query("SELECT c FROM Conversation c JOIN c.participants p WHERE p = :user ORDER BY c.lastActivityAt DESC")
    List<Conversation> findByParticipantOrderByLastActivityDesc(@Param("user") User user);
    
    @Query("SELECT c FROM Conversation c JOIN c.participants p1 JOIN c.participants p2 " +
           "WHERE p1 = :user1 AND p2 = :user2 AND c.isGroup = false")
    Optional<Conversation> findDirectConversationBetweenUsers(@Param("user1") User user1, @Param("user2") User user2);
    
    @Query("SELECT c FROM Conversation c JOIN c.participants p WHERE p = :user AND c.id = :conversationId")
    Optional<Conversation> findByParticipantAndId(@Param("user") User user, @Param("conversationId") UUID conversationId);
    
    @Query("SELECT COUNT(c) FROM Conversation c JOIN c.participants p WHERE p = :user")
    long countByParticipant(@Param("user") User user);
}
