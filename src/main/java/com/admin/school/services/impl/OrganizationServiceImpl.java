package com.admin.school.services.impl;

import com.admin.school.dto.organization.OrganizationProfileDTO;
import com.admin.school.models.Organization;
import com.admin.school.repository.OrganizationRepository;
import com.admin.school.services.OrganizationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;

    public OrganizationServiceImpl(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @Override
    public Organization completeProfile(String organizationId, OrganizationProfileDTO profileDTO) {
        log.info("Completing profile for organization with id: {}", organizationId);
        log.info("Profile data: {}", profileDTO);
        
        Organization organization = organizationRepository.findById(UUID.fromString(organizationId))
                .orElseThrow(() -> new RuntimeException("Organization not found"));
        
        log.info("Found organization: {}", organization.getEmail());
        
        // Update organization profile information
        if (profileDTO.getName() != null && !profileDTO.getName().trim().isEmpty()) {
            organization.setName(profileDTO.getName());
            log.info("Updated name to: {}", profileDTO.getName());
        }
        
        if (profileDTO.getAddress() != null && !profileDTO.getAddress().trim().isEmpty()) {
            organization.setAddress(profileDTO.getAddress());
            log.info("Updated address to: {}", profileDTO.getAddress());
        }
        
        if (profileDTO.getCountry() != null && !profileDTO.getCountry().trim().isEmpty()) {
            organization.setCountry(profileDTO.getCountry());
            log.info("Updated country to: {}", profileDTO.getCountry());
        }
        
        if (profileDTO.getState() != null && !profileDTO.getState().trim().isEmpty()) {
            organization.setState(profileDTO.getState());
            log.info("Updated state to: {}", profileDTO.getState());
        }
        
        if (profileDTO.getCity() != null && !profileDTO.getCity().trim().isEmpty()) {
            organization.setCity(profileDTO.getCity());
            log.info("Updated city to: {}", profileDTO.getCity());
        }
        
        if (profileDTO.getPhone() != null && !profileDTO.getPhone().trim().isEmpty()) {
            organization.setPhone(profileDTO.getPhone());
            log.info("Updated phone to: {}", profileDTO.getPhone());
        }
        
        if (profileDTO.getProfilePictureUrl() != null && !profileDTO.getProfilePictureUrl().trim().isEmpty()) {
            organization.setProfilePictureUrl(profileDTO.getProfilePictureUrl());
            log.info("Updated profile picture URL to: {}", profileDTO.getProfilePictureUrl());
        }
        
        Organization savedOrganization = organizationRepository.save(organization);
        log.info("Successfully saved organization with updated profile");
        return savedOrganization;
    }

    @Override
    public Organization getOrganizationById(UUID id) {
        return organizationRepository.findById(id).orElseThrow(() -> new RuntimeException("Organization not found"));
    }
} 