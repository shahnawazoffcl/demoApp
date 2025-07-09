package com.admin.school.dto.post;


import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PostRequestDTO {

    private String title;
    private String content;
    private String userId;
    private String organizationId;
}
