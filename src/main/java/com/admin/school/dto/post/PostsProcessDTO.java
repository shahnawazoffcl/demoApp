package com.admin.school.dto.post;


import com.admin.school.models.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;


@Setter
@Getter
@AllArgsConstructor
public class PostsProcessDTO {

    private Post post;
    private boolean like;
}
