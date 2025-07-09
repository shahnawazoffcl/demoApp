package com.admin.school.controllers.utils;

import com.admin.school.dto.post.PostRequestDTO;
import com.admin.school.dto.post.PostResponseDTO;
import com.admin.school.dto.post.PostsProcessDTO;
import com.admin.school.dto.post.FeedPostDTO;
import com.admin.school.dto.user.UserPostResponseDTO;
import com.admin.school.models.Post;
import com.admin.school.services.UserService;

import java.util.Date;

public class PostControllerUtils {

    public static Post mapPostRequestToPostModel(PostRequestDTO postRequestDTO) {
        Post post = new Post();
        post.setTitle(postRequestDTO.getTitle());
        post.setContent(postRequestDTO.getContent());
        return post;
    }

    public static PostResponseDTO mapPostToPostResponseDTO(Post post) {
        PostResponseDTO postResponseDTO = new PostResponseDTO();
        postResponseDTO.setId(post.getId());
        postResponseDTO.setTitle(post.getTitle());
        postResponseDTO.setContent(post.getContent());
        
        // Handle user information with null checks
        if (post.getUser() != null) {
            UserPostResponseDTO author = new UserPostResponseDTO();
            author.setId(post.getUser().getId().toString());
            author.setName(post.getUser().getUsername());
            author.setEmail(post.getUser().getEmail());
            author.setAvatarURL(post.getUser().getProfilePictureUrl());
            postResponseDTO.setAuthor(author);
        }
        
        postResponseDTO.setMediaUrl(post.getMediaUrl());
        postResponseDTO.setAudioUrl(post.getAudioUrl());
        postResponseDTO.setLikesCount(post.getLikes() != null ? post.getLikes().size() : 0);
        postResponseDTO.setCommentsCount(post.getComments() != null ? post.getComments().size() : 0);
        postResponseDTO.setSharesCount(0); // TODO: Implement shares functionality
        postResponseDTO.setCreatedAt(post.getCreatedAt());
        
        // Determine media type based on file extension and audio presence
        if (post.getMediaUrl() != null) {
            String mediaUrl = post.getMediaUrl().toLowerCase();
            if (post.getAudioUrl() != null) {
                // If both image and audio are present, treat as image with audio
                postResponseDTO.setMediaType("image");
            } else if (mediaUrl.contains(".mp4") || mediaUrl.contains(".avi") || mediaUrl.contains(".mov")) {
                postResponseDTO.setMediaType("video");
            } else if (mediaUrl.contains(".mp3") || mediaUrl.contains(".wav") || mediaUrl.contains(".ogg")) {
                postResponseDTO.setMediaType("audio");
            } else {
                postResponseDTO.setMediaType("image");
            }
        }
        
        return postResponseDTO;
    }

    public static PostResponseDTO mapPostToPostResponseDTO(Post post, String currentUserId, UserService userService) {
        PostResponseDTO postResponseDTO = mapPostToPostResponseDTO(post);
        
        // Set connection information
        if (post.getUser() != null && currentUserId != null) {
            boolean isConnected = userService.areUsersConnected(post.getUser().getId().toString(), currentUserId);
            postResponseDTO.setIsConnected(isConnected);
            postResponseDTO.setCanConnect(!isConnected && !post.getUser().getId().toString().equals(currentUserId));
        }
        
        return postResponseDTO;
    }

    public static PostResponseDTO mapPostProcessDTOToPostResponseDTO(PostsProcessDTO postDTO) {
        PostResponseDTO postResponseDTO = new PostResponseDTO();
        Post post = postDTO.getPost();
        
        postResponseDTO.setId(post.getId());
        postResponseDTO.setTitle(post.getTitle());
        postResponseDTO.setContent(post.getContent());
        postResponseDTO.setLiked(postDTO.isLike());
        postResponseDTO.setMediaUrl(post.getMediaUrl());
        postResponseDTO.setAudioUrl(post.getAudioUrl());
        postResponseDTO.setLikesCount(post.getLikes() != null ? post.getLikes().size() : 0);
        postResponseDTO.setCommentsCount(post.getComments() != null ? post.getComments().size() : 0);
        postResponseDTO.setSharesCount(0); // TODO: Implement shares functionality
        postResponseDTO.setCreatedAt(post.getCreatedAt());
        
        // Handle user information with null checks
        if (post.getUser() != null) {
            UserPostResponseDTO author = new UserPostResponseDTO();
            author.setId(post.getUser().getId().toString());
            author.setName(post.getUser().getUsername());
            author.setEmail(post.getUser().getEmail());
            author.setAvatarURL(post.getUser().getProfilePictureUrl());
            postResponseDTO.setAuthor(author);
        }
        
        // Determine media type based on file extension and audio presence
        if (post.getMediaUrl() != null) {
            String mediaUrl = post.getMediaUrl().toLowerCase();
            if (post.getAudioUrl() != null) {
                // If both image and audio are present, treat as image with audio
                postResponseDTO.setMediaType("image");
            } else if (mediaUrl.contains(".mp4") || mediaUrl.contains(".avi") || mediaUrl.contains(".mov")) {
                postResponseDTO.setMediaType("video");
            } else if (mediaUrl.contains(".mp3") || mediaUrl.contains(".wav") || mediaUrl.contains(".ogg")) {
                postResponseDTO.setMediaType("audio");
            } else {
                postResponseDTO.setMediaType("image");
            }
        }
        
        return postResponseDTO;
    }

    public static FeedPostDTO mapPostToFeedPostDTO(Post post, boolean isLiked, int relevanceScore, int actualLikesCount) {
        FeedPostDTO feedPostDTO = new FeedPostDTO();
        feedPostDTO.setId(post.getId());
        feedPostDTO.setTitle(post.getTitle());
        feedPostDTO.setContent(post.getContent());
        feedPostDTO.setLiked(isLiked);
        feedPostDTO.setRelevanceScore(relevanceScore);
        
        // Set post source based on relevance score
        switch (relevanceScore) {
            case 3:
                feedPostDTO.setPostSource("own");
                break;
            case 2:
                feedPostDTO.setPostSource("connection");
                break;
            case 1:
                feedPostDTO.setPostSource("organization");
                break;
            default:
                feedPostDTO.setPostSource("network");
                break;
        }
        
        // Check if post is recent (less than 24 hours)
        Date now = new Date();
        long hoursDiff = (now.getTime() - post.getCreatedAt().getTime()) / (1000 * 60 * 60);
        feedPostDTO.setIsRecent(hoursDiff < 24);
        
        // Handle user information with null checks
        if (post.getUser() != null) {
            UserPostResponseDTO author = new UserPostResponseDTO();
            author.setId(post.getUser().getId().toString());
            author.setName(post.getUser().getUsername());
            author.setEmail(post.getUser().getEmail());
            author.setAvatarURL(post.getUser().getProfilePictureUrl());
            feedPostDTO.setAuthor(author);
        }
        
        feedPostDTO.setMediaUrl(post.getMediaUrl());
        feedPostDTO.setLikesCount(actualLikesCount);
        feedPostDTO.setCommentsCount(post.getComments() != null ? post.getComments().size() : 0);
        feedPostDTO.setSharesCount(0); // TODO: Implement shares functionality
        feedPostDTO.setCreatedAt(post.getCreatedAt());
        
        // Determine media type based on file extension
        if (post.getMediaUrl() != null) {
            String mediaUrl = post.getMediaUrl().toLowerCase();
            if (mediaUrl.contains(".mp4") || mediaUrl.contains(".avi") || mediaUrl.contains(".mov")) {
                feedPostDTO.setMediaType("video");
            } else if (mediaUrl.contains(".mp3") || mediaUrl.contains(".wav") || mediaUrl.contains(".ogg")) {
                feedPostDTO.setMediaType("audio");
            } else {
                feedPostDTO.setMediaType("image");
            }
        }
        
        return feedPostDTO;
    }
}
