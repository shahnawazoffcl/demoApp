package com.admin.school.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class CommentLike extends BaseModel {

    @ManyToOne
    private Comment comment;

    @ManyToOne
    private User user;

    @ManyToOne
    private Organization organization;
} 