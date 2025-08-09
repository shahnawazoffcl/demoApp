package com.admin.school.services.impl;

import com.admin.school.dto.user.UserSchoolRelationshipDTO;
import com.admin.school.models.UserSchoolRelationship;
import com.admin.school.models.User;
import com.admin.school.models.Organization;
import com.admin.school.models.UserSchoolRole;
import com.admin.school.models.RelationshipStatus;
import com.admin.school.repository.UserSchoolRelationshipRepository;
import com.admin.school.repository.UserRepository;
import com.admin.school.repository.OrganizationRepository;
import com.admin.school.services.UserSchoolRelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class UserSchoolRelationshipServiceImpl implements UserSchoolRelationshipService {

    @Autowired
    private UserSchoolRelationshipRepository relationshipRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrganizationRepository organizationRepository;

    @Override
    public UserSchoolRelationshipDTO createRelationship(UserSchoolRelationshipDTO relationshipDTO) {
        System.out.println("Creating relationship with DTO: " + relationshipDTO);
        
        User user = userRepository.findById(UUID.fromString(relationshipDTO.getUserId()))
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        System.out.println("Found user: " + user.getEmail());
        
        // Find or create organization/school
        Organization school;
        if (relationshipDTO.getSchoolId() != null && !relationshipDTO.getSchoolId().isEmpty()) {
            // Try to find existing organization by ID
            school = organizationRepository.findById(UUID.fromString(relationshipDTO.getSchoolId()))
                    .orElseThrow(() -> new RuntimeException("School not found"));
        } else if (relationshipDTO.getSchoolName() != null && !relationshipDTO.getSchoolName().isEmpty()) {
            // Try to find existing organization by name, or create new one
            System.out.println("Looking for school with name: " + relationshipDTO.getSchoolName());
            school = organizationRepository.findByName(relationshipDTO.getSchoolName())
                    .orElseGet(() -> {
                        System.out.println("Creating new organization with name: " + relationshipDTO.getSchoolName());
                        Organization newSchool = new Organization();
                        newSchool.setName(relationshipDTO.getSchoolName());
                        newSchool.setEmail(relationshipDTO.getSchoolName().toLowerCase().replace(" ", "") + "@school.com");
                        newSchool.setPassword("defaultPassword123");
                        // Set location data if available (these would need to be added to the DTO)
                        // For now, we'll create without location data
                        Organization savedSchool = organizationRepository.save(newSchool);
                        System.out.println("Created organization with ID: " + savedSchool.getId());
                        return savedSchool;
                    });
        } else {
            throw new RuntimeException("Either schoolId or schoolName must be provided");
        }

        // If this is a current relationship, deactivate previous current relationships
        if (relationshipDTO.isCurrent()) {
            deactivateCurrentRelationships(user.getId());
        }

        UserSchoolRelationship relationship = new UserSchoolRelationship();
        relationship.setUser(user);
        relationship.setSchool(school);
        relationship.setRole(UserSchoolRole.valueOf(relationshipDTO.getRole().toUpperCase()));
        relationship.setStatus(RelationshipStatus.valueOf(relationshipDTO.getStatus()));
        relationship.setStartDate(relationshipDTO.getStartDate() != null ? relationshipDTO.getStartDate() : new Date());
        relationship.setEndDate(relationshipDTO.getEndDate());
        relationship.setGrade(relationshipDTO.getGrade());
        relationship.setSubject(relationshipDTO.getSubject());
        relationship.setDepartment(relationshipDTO.getDepartment());
        relationship.setCurrent(relationshipDTO.isCurrent());

        UserSchoolRelationship savedRelationship = relationshipRepository.save(relationship);
        System.out.println("Saved relationship with ID: " + savedRelationship.getId());
        UserSchoolRelationshipDTO result = mapToDTO(savedRelationship);
        System.out.println("Returning DTO: " + result);
        return result;
    }

    @Override
    public UserSchoolRelationshipDTO updateRelationship(String relationshipId, UserSchoolRelationshipDTO relationshipDTO) {
        UserSchoolRelationship relationship = relationshipRepository.findById(UUID.fromString(relationshipId))
                .orElseThrow(() -> new RuntimeException("Relationship not found"));

        if (relationshipDTO.getRole() != null) {
            relationship.setRole(UserSchoolRole.valueOf(relationshipDTO.getRole()));
        }
        if (relationshipDTO.getStatus() != null) {
            relationship.setStatus(RelationshipStatus.valueOf(relationshipDTO.getStatus()));
        }
        if (relationshipDTO.getStartDate() != null) {
            relationship.setStartDate(relationshipDTO.getStartDate());
        }
        if (relationshipDTO.getEndDate() != null) {
            relationship.setEndDate(relationshipDTO.getEndDate());
        }
        if (relationshipDTO.getGrade() != null) {
            relationship.setGrade(relationshipDTO.getGrade());
        }
        if (relationshipDTO.getSubject() != null) {
            relationship.setSubject(relationshipDTO.getSubject());
        }
        if (relationshipDTO.getDepartment() != null) {
            relationship.setDepartment(relationshipDTO.getDepartment());
        }

        // Handle current relationship changes
        if (relationshipDTO.isCurrent() && !relationship.isCurrent()) {
            deactivateCurrentRelationships(relationship.getUser().getId());
            relationship.setCurrent(true);
        } else if (!relationshipDTO.isCurrent() && relationship.isCurrent()) {
            relationship.setCurrent(false);
        }

        UserSchoolRelationship updatedRelationship = relationshipRepository.save(relationship);
        return mapToDTO(updatedRelationship);
    }

    @Override
    public void deleteRelationship(String relationshipId) {
        relationshipRepository.deleteById(UUID.fromString(relationshipId));
    }

    @Override
    public UserSchoolRelationshipDTO getRelationshipById(String relationshipId) {
        UserSchoolRelationship relationship = relationshipRepository.findById(UUID.fromString(relationshipId))
                .orElseThrow(() -> new RuntimeException("Relationship not found"));
        return mapToDTO(relationship);
    }

    @Override
    public List<UserSchoolRelationshipDTO> getUserRelationships(String userId) {
        List<UserSchoolRelationship> relationships = relationshipRepository.findByUserIdOrderByStartDateDesc(UUID.fromString(userId));
        return relationships.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<UserSchoolRelationshipDTO> getSchoolRelationships(String schoolId) {
        List<UserSchoolRelationship> relationships = relationshipRepository.findBySchoolIdOrderByStartDateDesc(UUID.fromString(schoolId));
        return relationships.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public UserSchoolRelationshipDTO getCurrentUserRelationship(String userId) {
        Optional<UserSchoolRelationship> relationship = relationshipRepository.findCurrentByUserId(UUID.fromString(userId));
        return relationship.map(this::mapToDTO).orElse(null);
    }

    @Override
    public List<UserSchoolRelationshipDTO> getUserRelationshipsByRole(String userId, UserSchoolRole role) {
        List<UserSchoolRelationship> relationships = relationshipRepository.findByUserIdAndRole(UUID.fromString(userId), role);
        return relationships.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<UserSchoolRelationshipDTO> getSchoolRelationshipsByRole(String schoolId, UserSchoolRole role) {
        List<UserSchoolRelationship> relationships = relationshipRepository.findBySchoolIdAndRoleAndStatus(
                UUID.fromString(schoolId), role, RelationshipStatus.ACTIVE);
        return relationships.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public void transferUserToNewSchool(String userId, String newSchoolId, UserSchoolRole role) {
        // End current relationship
        Optional<UserSchoolRelationship> currentRelationship = relationshipRepository.findCurrentByUserId(UUID.fromString(userId));
        if (currentRelationship.isPresent()) {
            UserSchoolRelationship current = currentRelationship.get();
            current.setStatus(RelationshipStatus.TRANSFERRED);
            current.setEndDate(new Date());
            current.setCurrent(false);
            relationshipRepository.save(current);
        }

        // Create new relationship
        UserSchoolRelationshipDTO newRelationshipDTO = new UserSchoolRelationshipDTO();
        newRelationshipDTO.setUserId(userId);
        newRelationshipDTO.setSchoolId(newSchoolId);
        newRelationshipDTO.setRole(role.name());
        newRelationshipDTO.setStatus(RelationshipStatus.ACTIVE.name());
        newRelationshipDTO.setStartDate(new Date());
        newRelationshipDTO.setCurrent(true);

        createRelationship(newRelationshipDTO);
    }

    @Override
    public void graduateUser(String userId) {
        Optional<UserSchoolRelationship> currentRelationship = relationshipRepository.findCurrentByUserId(UUID.fromString(userId));
        if (currentRelationship.isPresent()) {
            UserSchoolRelationship current = currentRelationship.get();
            current.setStatus(RelationshipStatus.GRADUATED);
            current.setEndDate(new Date());
            current.setCurrent(false);
            relationshipRepository.save(current);
        }
    }

    @Override
    public int getSchoolUserCount(String schoolId, UserSchoolRole role, RelationshipStatus status) {
        return relationshipRepository.countBySchoolIdAndRoleAndStatus(UUID.fromString(schoolId), role, status);
    }

    @Override
    public List<User> getOrganizationStaff(String organizationId) {
        List<UserSchoolRelationship> relationships = relationshipRepository.findBySchoolIdOrderByStartDateDesc(UUID.fromString(organizationId));
        return relationships.stream()
                .filter(rel -> rel.getStatus() == RelationshipStatus.ACTIVE &&
                        (rel.getRole() == UserSchoolRole.TEACHER || rel.getRole() == UserSchoolRole.STAFF))
                .map(UserSchoolRelationship::getUser)
                .distinct()
                .collect(Collectors.toList());
    }

    private void deactivateCurrentRelationships(UUID userId) {
        Optional<UserSchoolRelationship> currentRelationship = relationshipRepository.findCurrentByUserId(userId);
        if (currentRelationship.isPresent()) {
            UserSchoolRelationship current = currentRelationship.get();
            current.setCurrent(false);
            relationshipRepository.save(current);
        }
    }

    private UserSchoolRelationshipDTO mapToDTO(UserSchoolRelationship relationship) {
        UserSchoolRelationshipDTO dto = new UserSchoolRelationshipDTO();
        dto.setId(relationship.getId().toString());
        dto.setUserId(relationship.getUser().getId().toString());
        dto.setSchoolId(relationship.getSchool().getId().toString());
        dto.setSchoolName(relationship.getSchool().getName());
        dto.setRole(relationship.getRole().name());
        dto.setStatus(relationship.getStatus().name());
        dto.setStartDate(relationship.getStartDate());
        dto.setEndDate(relationship.getEndDate());
        dto.setGrade(relationship.getGrade());
        dto.setSubject(relationship.getSubject());
        dto.setDepartment(relationship.getDepartment());
        dto.setCurrent(relationship.isCurrent());
        return dto;
    }

    @Override
    public List<String> getAllCountries() {
        return organizationRepository.findAllCountries();
    }
    @Override
    public List<String> getAllStatesByCountry(String country) {
        return organizationRepository.findAllStatesByCountry(country);
    }

    @Override
    public List<String> getAllCitiesByState(String state) {
        return organizationRepository.findAllCitiesByState(state);
    }

    @Override
    public List<Organization> getByCountryAndStateAndCityAndNameContainingIgnoreCase(String country, String state, String city, String name) {
        if (country != null && state != null && city != null && name != null) {
            return organizationRepository.findByCountryAndStateAndCityAndNameContainingIgnoreCase(country, state, city, name);
        } else if (country != null && state != null && city != null) {
            return organizationRepository.findByCountryAndStateAndCity(country, state, city);
        } else if (country != null && state != null) {
            return organizationRepository.findByCountryAndState(country, state);
        } else if (country != null) {
            return organizationRepository.findByCountry(country);
        } else {
            return organizationRepository.findAll();
        }
    }

    @Override
    public boolean hasActiveRelationship(UUID userId, UUID organizationId) {
        return relationshipRepository.existsByUserIdAndSchoolIdAndStatus(userId, organizationId, RelationshipStatus.ACTIVE);
    }
} 