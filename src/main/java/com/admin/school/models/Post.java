package com.admin.school.models;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
public class Post extends BaseModel{

    private String title;
    private ContentType contentType;
    private String content;
    private String mediaUrl;
    private String audioUrl;
    @OneToMany
    private List<Comment> comments = new ArrayList<>();
    @ManyToOne
    private User user;
    @ManyToOne
    private Organization organization;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PostLike> likes = new ArrayList<>();

}
