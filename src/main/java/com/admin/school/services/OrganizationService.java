package com.admin.school.services;

import com.admin.school.dto.organization.OrganizationProfileDTO;
import com.admin.school.models.Organization;

import java.util.UUID;

public interface OrganizationService {
    Organization completeProfile(String organizationId, OrganizationProfileDTO profileDTO);

    Organization getOrganizationById(UUID id);
} 