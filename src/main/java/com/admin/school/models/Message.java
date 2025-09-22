package com.admin.school.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
public class Message extends BaseModel {
    
    @ManyToOne
    private User sender;
    
    @ManyToOne
    private Conversation conversation;
    
    private String content;
    
    @Enumerated(EnumType.STRING)
    private MessageType messageType = MessageType.TEXT;
    
    private String mediaUrl; // For images, files, etc.
    
    @OneToMany(mappedBy = "message", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<MessageStatus> messageStatuses = new ArrayList<>();
    
    @ManyToOne
    private Message replyTo; // For reply functionality
    
    private boolean isEdited = false;
    
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date editedAt;
    
    public enum MessageType {
        TEXT, IMAGE, FILE, AUDIO, VIDEO
    }
}
