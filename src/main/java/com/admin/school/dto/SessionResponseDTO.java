package com.admin.school.dto;


import com.admin.school.dto.user.UserResponseDTO;
import com.admin.school.dto.organization.OrganizationResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SessionResponseDTO {

    private String token;
    private UserResponseDTO author;
    private OrganizationResponseDTO organization; // For organization data
    private String entityType; // "USER" or "ORGANIZATION"
    private Date expiryAt;
    private boolean isRegistered;
}
