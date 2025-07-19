package com.admin.school.repository;

import com.admin.school.models.CommentLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CommentLikeRepository extends JpaRepository<CommentLike, UUID> {

    @Query("SELECT cl FROM CommentLike cl WHERE cl.comment.id = :commentId AND cl.user.id = :userId")
    Optional<CommentLike> findByCommentIdAndUserId(@Param("commentId") UUID commentId, @Param("userId") UUID userId);

    @Query("SELECT COUNT(cl) FROM CommentLike cl WHERE cl.comment.id = :commentId")
    int countByCommentId(@Param("commentId") UUID commentId);

    @Query("SELECT cl FROM CommentLike cl WHERE cl.comment.id = :commentId")
    List<CommentLike> findByCommentId(@Param("commentId") UUID commentId);

    void deleteByCommentId(UUID commentId);
} 