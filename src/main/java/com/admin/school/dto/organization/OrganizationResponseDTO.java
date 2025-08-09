package com.admin.school.dto.organization;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationResponseDTO {
    private String id;
    private String name;
    private String email;
    private String address;
    private String phone;
    private String profilePictureUrl;
    private String role;
    private Date createdAt;
    private String profileStatus;
} 