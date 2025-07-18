package com.admin.school.models;


import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Notification extends BaseModel{
    private String title;
    private String content;
    private String type;
    private boolean readStatus;
    @ManyToOne
    private User recipient;
    @ManyToOne
    private User sender;
    @ManyToOne
    private Post post;
    @ManyToOne
    private Comment comment;

}
