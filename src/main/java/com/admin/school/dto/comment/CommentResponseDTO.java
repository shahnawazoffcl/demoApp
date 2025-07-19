package com.admin.school.dto.comment;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;

@Getter
@Setter
public class CommentResponseDTO {
    private String id;
    private String content;
    private String authorId;
    private String authorName;
    private String authorAvatarURL;
    private String postId;
    private Date createdAt;
    private int likesCount;
    private boolean liked;
    private String organizationId;
    private String organizationName;
} 