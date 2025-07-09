package com.admin.school.dto.search;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class UserSearchResult {
    private UUID id;
    private String username;
    private String email;
    private String role;
    private String profilePictureUrl;
    private boolean isConnected;
    private int mutualConnections;
    private String relevanceScore; // "high", "medium", "low"
    private boolean canConnect; // true if user can send connection request

    // Getters
    public UUID getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getRole() { return role; }
    public String getProfilePictureUrl() { return profilePictureUrl; }
    public boolean isConnected() { return isConnected; }
    public int getMutualConnections() { return mutualConnections; }
    public String getRelevanceScore() { return relevanceScore; }
    public boolean isCanConnect() { return canConnect; }

    // Setters
    public void setId(UUID id) { this.id = id; }
    public void setUsername(String username) { this.username = username; }
    public void setEmail(String email) { this.email = email; }
    public void setRole(String role) { this.role = role; }
    public void setProfilePictureUrl(String profilePictureUrl) { this.profilePictureUrl = profilePictureUrl; }
    public void setIsConnected(boolean isConnected) { this.isConnected = isConnected; }
    public void setMutualConnections(int mutualConnections) { this.mutualConnections = mutualConnections; }
    public void setRelevanceScore(String relevanceScore) { this.relevanceScore = relevanceScore; }
    public void setCanConnect(boolean canConnect) { this.canConnect = canConnect; }
} 