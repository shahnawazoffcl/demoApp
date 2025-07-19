package com.admin.school.services.impl;

import com.admin.school.dto.comment.CommentRequestDTO;
import com.admin.school.dto.comment.CommentResponseDTO;
import com.admin.school.models.Comment;
import com.admin.school.models.CommentLike;
import com.admin.school.models.Post;
import com.admin.school.models.User;
import com.admin.school.models.Organization;
import com.admin.school.repository.CommentRepository;
import com.admin.school.repository.CommentLikeRepository;
import com.admin.school.repository.PostsRepository;
import com.admin.school.repository.UserRepository;
import com.admin.school.repository.OrganizationRepository;
import com.admin.school.services.CommentService;
import com.admin.school.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private CommentLikeRepository commentLikeRepository;

    @Autowired
    private PostsRepository postsRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    private final NotificationService notificationService;

    public CommentServiceImpl(NotificationService notificationService) {
        this.notificationService = notificationService;
    }


    @Override
    public CommentResponseDTO createComment(CommentRequestDTO commentRequestDTO, String authorType) {
        Comment comment = new Comment();
        comment.setContent(commentRequestDTO.getContent());
        String authorId = commentRequestDTO.getUserId();
        
        // Set the post
        Post post = postsRepository.findById(UUID.fromString(commentRequestDTO.getPostId()))
                .orElseThrow(() -> new RuntimeException("Post not found"));
        comment.setPost(post);
        
        if ("USER".equals(authorType)) {
            User user = userRepository.findById(UUID.fromString(authorId))
                    .orElseThrow(() -> new RuntimeException("User not found"));
            comment.setAuthor(user);
        } else if ("ORGANIZATION".equals(authorType)) {
            Organization organization = organizationRepository.findById(UUID.fromString(authorId))
                    .orElseThrow(() -> new RuntimeException("Organization not found"));
            comment.setOrg(organization);
        }
        
        Comment savedComment = commentRepository.save(comment);
        if( savedComment.getAuthor() != null && !savedComment.getPost().getUser().getId().toString().equals(authorId)) {
            notificationService.sendCommentNotification(savedComment, savedComment.getAuthor());
        } else if (savedComment.getOrg() != null && !savedComment.getPost().getOrganization().getId().toString().equals(authorId)) {
            notificationService.sendCommentNotification(savedComment, savedComment.getOrg());
        }

        return mapCommentToResponseDTO(savedComment, authorId);
    }

    @Override
    public List<CommentResponseDTO> getCommentsByPostId(String postId, String userId) {
        List<Comment> comments = commentRepository.findByPostIdOrderByCreatedAtDesc(UUID.fromString(postId));
        return comments.stream()
                .map(comment -> mapCommentToResponseDTO(comment, userId))
                .collect(Collectors.toList());
    }

    @Override
    public void deleteComment(String commentId, String userId) {
        Comment comment = commentRepository.findById(UUID.fromString(commentId))
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        
        // Check if user is the author of the comment
        if (comment.getAuthor() != null && comment.getAuthor().getId().equals(UUID.fromString(userId))) {
            commentRepository.delete(comment);
        } else if (comment.getOrg() != null && comment.getOrg().getId().equals(UUID.fromString(userId))) {
            commentRepository.delete(comment);
        } else {
            throw new RuntimeException("Unauthorized to delete this comment");
        }
    }

    public CommentResponseDTO likeComment(String commentId, String userId) {
        Comment comment = commentRepository.findById(UUID.fromString(commentId))
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Check if already liked
        Optional<CommentLike> existingLike = commentLikeRepository.findByCommentIdAndUserId(UUID.fromString(commentId), UUID.fromString(userId));
        if (existingLike.isPresent()) {
            return mapCommentToResponseDTO(comment, String.valueOf(userId));
        }
        
        // Create new like
        CommentLike commentLike = new CommentLike();
        commentLike.setComment(comment);
        commentLike.setUser(user);
        commentLikeRepository.save(commentLike);
        
        // Update like count
        comment.setLikesCount(comment.getLikesCount() + 1);
        commentRepository.save(comment);
        
        return mapCommentToResponseDTO(comment, userId);
    }

    @Override
    public CommentResponseDTO unlikeComment(String commentId, String userId) {
        Comment comment = commentRepository.findById(UUID.fromString(commentId))
                .orElseThrow(() -> new RuntimeException("Comment not found"));
        
        // Remove like
        Optional<CommentLike> existingLike = commentLikeRepository.findByCommentIdAndUserId(UUID.fromString(commentId), UUID.fromString(userId));
        if (existingLike.isPresent()) {
            commentLikeRepository.delete(existingLike.get());
            
            // Update like count
            comment.setLikesCount(Math.max(0, comment.getLikesCount() - 1));
            commentRepository.save(comment);
        }
        
        return mapCommentToResponseDTO(comment, userId);
    }

    @Override
    public int getCommentCountByPostId(String postId) {
        return commentRepository.countByPostId(UUID.fromString(postId));
    }

    private CommentResponseDTO mapCommentToResponseDTO(Comment comment, String currentUserId) {
        CommentResponseDTO responseDTO = new CommentResponseDTO();
        responseDTO.setId(String.valueOf(comment.getId()));
        responseDTO.setContent(comment.getContent());
        responseDTO.setPostId(String.valueOf(comment.getPost().getId()));
        responseDTO.setCreatedAt(comment.getCreatedAt());
        responseDTO.setLikesCount(comment.getLikesCount());
        
        // Set author information
        if (comment.getAuthor() != null) {
            responseDTO.setAuthorId(String.valueOf(comment.getAuthor().getId()));
            responseDTO.setAuthorName(comment.getAuthor().getUsername());
            responseDTO.setAuthorAvatarURL(comment.getAuthor().getProfilePictureUrl());
        } else if (comment.getOrg() != null) {
            responseDTO.setOrganizationId(String.valueOf(comment.getOrg().getId()));
            responseDTO.setOrganizationName(comment.getOrg().getName());
        }
        
        // Check if current user liked this comment
        Optional<CommentLike> userLike = commentLikeRepository.findByCommentIdAndUserId(comment.getId(), UUID.fromString(currentUserId));
        responseDTO.setLiked(userLike.isPresent());
        
        return responseDTO;
    }
} 