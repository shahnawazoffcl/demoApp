package com.admin.school.dto.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CompleteProfileDTO {
    private String email;
    private String phone;
    private String address;
    private String profilePicture;
    private String role;
} 