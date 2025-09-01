package com.admin.school.services;

import com.admin.school.dto.user.UserSchoolRelationshipDTO;
import com.admin.school.models.Organization;
import com.admin.school.models.User;
import com.admin.school.models.UserSchoolRole;
import com.admin.school.models.RelationshipStatus;

import java.util.List;
import java.util.UUID;

public interface UserSchoolRelationshipService {
    
    UserSchoolRelationshipDTO createRelationship(UserSchoolRelationshipDTO relationshipDTO);
    
    UserSchoolRelationshipDTO updateRelationship(String relationshipId, UserSchoolRelationshipDTO relationshipDTO);
    
    void deleteRelationship(String relationshipId, String deletedBy);
    
    UserSchoolRelationshipDTO getRelationshipById(String relationshipId);
    
    List<UserSchoolRelationshipDTO> getUserRelationships(String userId);
    
    List<UserSchoolRelationshipDTO> getSchoolRelationships(String schoolId);
    
    UserSchoolRelationshipDTO getCurrentUserRelationship(String userId);
    
    List<UserSchoolRelationshipDTO> getUserRelationshipsByRole(String userId, UserSchoolRole role);
    
    List<UserSchoolRelationshipDTO> getSchoolRelationshipsByRole(String schoolId, UserSchoolRole role);
    
    void transferUserToNewSchool(String userId, String newSchoolId, UserSchoolRole role);
    
    void graduateUser(String userId);
    
    int getSchoolUserCount(String schoolId, UserSchoolRole role, RelationshipStatus status);

    List<String> getAllCountries();

    List<String> getAllStatesByCountry(String country);

    List<String> getAllCitiesByState(String state);

    List<Organization> getByCountryAndStateAndCityAndNameContainingIgnoreCase(String country, String state, String city, String name);

    List<User> getOrganizationStaff(String organizationId);

    boolean hasActiveRelationship(UUID userId, UUID organizationId);
}