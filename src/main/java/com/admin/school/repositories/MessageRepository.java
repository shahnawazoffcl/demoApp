package com.admin.school.repositories;

import com.admin.school.models.Conversation;
import com.admin.school.models.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface MessageRepository extends JpaRepository<Message, UUID> {
    
    List<Message> findByConversationOrderByCreatedAtAsc(Conversation conversation);
    
    Page<Message> findByConversationOrderByCreatedAtDesc(Conversation conversation, Pageable pageable);
    
    @Query("SELECT m FROM Message m WHERE m.conversation = :conversation ORDER BY m.createdAt DESC")
    List<Message> findLatestMessagesByConversation(@Param("conversation") Conversation conversation, Pageable pageable);
    
    @Query("SELECT COUNT(m) FROM Message m WHERE m.conversation = :conversation")
    long countByConversation(@Param("conversation") Conversation conversation);
    
    @Query("SELECT m FROM Message m WHERE m.conversation.id = :conversationId AND m.id = :messageId")
    Optional<Message> findByConversationIdAndMessageId(@Param("conversationId") UUID conversationId, @Param("messageId") UUID messageId);
}
