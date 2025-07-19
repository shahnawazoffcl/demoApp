package com.admin.school.repository;

import com.admin.school.models.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface CommentRepository extends JpaRepository<Comment, UUID> {

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId ORDER BY c.createdAt DESC")
    List<Comment> findByPostIdOrderByCreatedAtDesc(@Param("postId") UUID postId);

    @Query("SELECT c FROM Comment c WHERE c.post.id = :postId AND c.author.id = :authorId")
    List<Comment> findByPostIdAndAuthorId(@Param("postId") UUID postId, @Param("authorId") UUID authorId);

    @Query("SELECT COUNT(c) FROM Comment c WHERE c.post.id = :postId")
    int countByPostId(@Param("postId") UUID postId);

    void deleteByPostId(UUID postId);
} 