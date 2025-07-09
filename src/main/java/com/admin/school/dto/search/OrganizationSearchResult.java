package com.admin.school.dto.search;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

public class OrganizationSearchResult {
    private UUID id;
    private String name;
    private String email;
    private String address;
    private String phone;
    private boolean isFollowing;
    private int followersCount;
    private String relevanceScore; // "high", "medium", "low"
    private boolean canFollow; // true if user can follow this org

    // Getters
    public UUID getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public boolean isFollowing() { return isFollowing; }
    public int getFollowersCount() { return followersCount; }
    public String getRelevanceScore() { return relevanceScore; }
    public boolean isCanFollow() { return canFollow; }

    // Setters
    public void setId(UUID id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }
    public void setAddress(String address) { this.address = address; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setIsFollowing(boolean isFollowing) { this.isFollowing = isFollowing; }
    public void setFollowersCount(int followersCount) { this.followersCount = followersCount; }
    public void setRelevanceScore(String relevanceScore) { this.relevanceScore = relevanceScore; }
    public void setCanFollow(boolean canFollow) { this.canFollow = canFollow; }
} 