package com.admin.school.services;

import com.admin.school.dto.comment.CommentRequestDTO;
import com.admin.school.dto.comment.CommentResponseDTO;

import java.util.List;
import java.util.UUID;

public interface CommentService {
    
    CommentResponseDTO createComment(CommentRequestDTO commentRequestDTO, String authorType);
    
    List<CommentResponseDTO> getCommentsByPostId(String postId, String userId);
    
    void deleteComment(String commentId, String userId);
    
    CommentResponseDTO likeComment(String commentId, String userId);
    
    CommentResponseDTO unlikeComment(String commentId, String userId);
    
    int getCommentCountByPostId(String postId);
} 