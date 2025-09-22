package com.admin.school.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class MessageStatus extends BaseModel {
    
    @ManyToOne
    private Message message;
    
    @ManyToOne
    private User user;
    
    @Enumerated(EnumType.STRING)
    private Status status = Status.SENT;
    
    @Temporal(TemporalType.TIMESTAMP)
    private java.util.Date readAt;
    
    public enum Status {
        SENT, DELIVERED, READ
    }
}
