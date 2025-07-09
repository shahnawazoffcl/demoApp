package com.admin.school.dto.post;

import com.admin.school.dto.user.UserPostResponseDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

public class FeedPostDTO {
    private UUID id;
    private String title;
    private String content;
    private UserPostResponseDTO author;
    private String mediaUrl;
    private String mediaType;
    private boolean liked;
    private int likesCount;
    private int commentsCount;
    private int sharesCount;
    private Date createdAt;
    private int relevanceScore; // 3=own post, 2=direct connection, 1=org following, 0=extended network
    private String postSource; // "own", "connection", "organization", "network"
    private boolean isRecent; // true if post is less than 24 hours old

    // Getters
    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public UserPostResponseDTO getAuthor() { return author; }
    public String getMediaUrl() { return mediaUrl; }
    public String getMediaType() { return mediaType; }
    public boolean isLiked() { return liked; }
    public int getLikesCount() { return likesCount; }
    public int getCommentsCount() { return commentsCount; }
    public int getSharesCount() { return sharesCount; }
    public Date getCreatedAt() { return createdAt; }
    public int getRelevanceScore() { return relevanceScore; }
    public String getPostSource() { return postSource; }
    public boolean isRecent() { return isRecent; }

    // Setters
    public void setId(UUID id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setContent(String content) { this.content = content; }
    public void setAuthor(UserPostResponseDTO author) { this.author = author; }
    public void setMediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; }
    public void setMediaType(String mediaType) { this.mediaType = mediaType; }
    public void setLiked(boolean liked) { this.liked = liked; }
    public void setLikesCount(int likesCount) { this.likesCount = likesCount; }
    public void setCommentsCount(int commentsCount) { this.commentsCount = commentsCount; }
    public void setSharesCount(int sharesCount) { this.sharesCount = sharesCount; }
    public void setCreatedAt(Date createdAt) { this.createdAt = createdAt; }
    public void setRelevanceScore(int relevanceScore) { this.relevanceScore = relevanceScore; }
    public void setPostSource(String postSource) { this.postSource = postSource; }
    public void setIsRecent(boolean isRecent) { this.isRecent = isRecent; }
} 