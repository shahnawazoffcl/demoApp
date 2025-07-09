package com.admin.school.dto.user;


import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserPostResponseDTO {
    private String id;
    private String name;
    private String email;
    private String avatarURL;
}
