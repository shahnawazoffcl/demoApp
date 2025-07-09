package com.admin.school.models;


import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
public class Comment extends BaseModel{

    private String content;
    @ManyToOne
    private User author;
    @ManyToOne
    private Organization org;
}
