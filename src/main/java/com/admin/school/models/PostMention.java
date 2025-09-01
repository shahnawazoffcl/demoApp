package com.admin.school.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class PostMention extends BaseModel {

    @ManyToOne(optional = false)
    private Post post;

    @ManyToOne(optional = false)
    private Organization organization;
} 