package com.admin.school.services;

import com.admin.school.dto.organization.OrganizationProfileDTO;
import com.admin.school.models.Organization;

public interface OrganizationService {
    Organization completeProfile(String organizationId, OrganizationProfileDTO profileDTO);
} 