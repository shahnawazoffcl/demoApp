package com.admin.school.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Setter
@Getter
public class Comment extends BaseModel{

    private String content;
    
    @ManyToOne
    private User author;
    
    @ManyToOne
    private Post post;
    
    @ManyToOne
    private Organization org;
    
//    @OneToMany(mappedBy = "comment", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
//    private List<CommentLike> likes = new ArrayList<>();
    
    private int likesCount = 0;
}
