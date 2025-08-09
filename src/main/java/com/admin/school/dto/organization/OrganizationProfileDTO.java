package com.admin.school.dto.organization;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrganizationProfileDTO {
    private String name;
    private String email;
    private String address;
    private String country;
    private String state;
    private String city;
    private String phone;
    private String profilePictureUrl;
} 