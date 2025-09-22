package com.admin.school.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
public class Conversation extends BaseModel {
    
    @ManyToMany
    @JoinTable(
        name = "conversation_participants",
        joinColumns = @JoinColumn(name = "conversation_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> participants = new ArrayList<>();
    
    @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Message> messages = new ArrayList<>();
    
    private String title; // For group conversations
    private boolean isGroup = false;
    
    @OneToOne
    private Message lastMessage;
    
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date lastActivityAt;
    
    @PrePersist
    protected void onCreate() {
        super.onCreate();
        if (lastActivityAt == null) {
            lastActivityAt = new java.util.Date();
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        super.onUpdate();
        lastActivityAt = new java.util.Date();
    }
}
