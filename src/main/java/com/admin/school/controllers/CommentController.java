package com.admin.school.controllers;

import com.admin.school.dto.comment.CommentRequestDTO;
import com.admin.school.dto.comment.CommentResponseDTO;
import com.admin.school.services.AuthService;
import com.admin.school.services.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/post")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private AuthService authService;

    @GetMapping("/{postId}/comments")
    public ResponseEntity<List<CommentResponseDTO>> getComments(
            @RequestHeader("token") String token,
            @PathVariable("postId") String postId) {
        try {
            String userId = authService.getUserIdFromToken(token);
            authService.validateUser(token, userId);
            
            List<CommentResponseDTO> comments = commentService.getCommentsByPostId(postId, userId);
            return ResponseEntity.ok(comments);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/{postId}/comments")
    public ResponseEntity<CommentResponseDTO> createComment(
            @RequestHeader("token") String token,
            @PathVariable("postId") String postId,
            @RequestBody CommentRequestDTO commentRequestDTO) {
        try {
            authService.validateUser(token, commentRequestDTO.getUserId());
            
            commentRequestDTO.setPostId(postId);

            CommentResponseDTO createdComment = commentService.createComment(commentRequestDTO, "USER");
            return ResponseEntity.ok(createdComment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/comments/{commentId}")
    public ResponseEntity<?> deleteComment(
            @RequestHeader("token") String token,
            @PathVariable("commentId") String commentId) {
        try {
            String userId = authService.getUserIdFromToken(token);
            authService.validateUser(token, userId);
            
            commentService.deleteComment(commentId, userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/comments/{commentId}/like/{userId}")
    public ResponseEntity<CommentResponseDTO> likeComment(
            @RequestHeader("token") String token,
            @PathVariable("commentId") String commentId,
            @PathVariable("userId") String userId) {
        try {
            authService.validateUser(token, userId);
            
            CommentResponseDTO updatedComment = commentService.likeComment(commentId, userId);
            return ResponseEntity.ok(updatedComment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/comments/{commentId}/like/{userId}")
    public ResponseEntity<CommentResponseDTO> unlikeComment(
            @RequestHeader("token") String token,
            @PathVariable("commentId") String commentId,
            @PathVariable("userId") String userId) {
        try {
            authService.validateUser(token, userId);
            
            CommentResponseDTO updatedComment = commentService.unlikeComment(commentId, userId);
            return ResponseEntity.ok(updatedComment);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 