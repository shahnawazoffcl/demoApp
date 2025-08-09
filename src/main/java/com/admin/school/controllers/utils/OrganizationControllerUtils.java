package com.admin.school.controllers.utils;

import com.admin.school.dto.organization.OrganizationResponseDTO;
import com.admin.school.models.Organization;

public class OrganizationControllerUtils {
    
    public static OrganizationResponseDTO mapOrganizationToOrganizationResponseDTO(Organization organization) {
        OrganizationResponseDTO dto = new OrganizationResponseDTO();
        dto.setId(organization.getId().toString());
        dto.setName(organization.getName());
        dto.setEmail(organization.getEmail());
        dto.setAddress(organization.getAddress());
        dto.setPhone(organization.getPhone());
        dto.setProfilePictureUrl(organization.getProfilePictureUrl());
        dto.setRole("ORGANIZATION");
        dto.setCreatedAt(organization.getCreatedAt());
        dto.setProfileStatus("COMPLETED"); // Organizations are always completed
        return dto;
    }
} 