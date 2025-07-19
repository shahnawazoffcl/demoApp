package com.admin.school.dto.comment;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CommentRequestDTO {
    private String content;
    private String userId;
    private String postId;
    private String organizationId;
} 