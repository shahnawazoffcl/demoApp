package com.admin.school.controllers;

import com.admin.school.dto.user.UserSchoolRelationshipDTO;
import com.admin.school.models.UserSchoolRole;
import com.admin.school.models.RelationshipStatus;
import com.admin.school.services.AuthService;
import com.admin.school.services.UserSchoolRelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user-school-relationship")
public class UserSchoolRelationshipController {

    @Autowired
    private UserSchoolRelationshipService relationshipService;

    @Autowired
    private AuthService authService;

    @PostMapping("/create")
    public ResponseEntity<UserSchoolRelationshipDTO> createRelationship(
            @RequestHeader("token") String token,
            @RequestBody UserSchoolRelationshipDTO relationshipDTO) {
        try {
            String userId = relationshipDTO.getUserId();
            authService.validateUser(token, userId);
            
            UserSchoolRelationshipDTO createdRelationship = relationshipService.createRelationship(relationshipDTO);
            return ResponseEntity.ok(createdRelationship);
        } catch (Exception e) {
            e.printStackTrace(); // Add logging to see the actual error
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{relationshipId}")
    public ResponseEntity<UserSchoolRelationshipDTO> updateRelationship(
            @RequestHeader("token") String token,
            @PathVariable("relationshipId") String relationshipId,
            @RequestBody UserSchoolRelationshipDTO relationshipDTO) {
        try {
            String userId = authService.getUserIdFromToken(token);
            authService.validateUser(token, userId);
            
            UserSchoolRelationshipDTO updatedRelationship = relationshipService.updateRelationship(relationshipId, relationshipDTO);
            return ResponseEntity.ok(updatedRelationship);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{relationshipId}")
    public ResponseEntity<?> deleteRelationship(
            @RequestHeader("token") String token,
            @PathVariable("relationshipId") String relationshipId) {
        try {
            String userId = authService.getUserIdFromToken(token);
            authService.validateUser(token, userId);
            
            relationshipService.deleteRelationship(relationshipId,userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{relationshipId}")
    public ResponseEntity<UserSchoolRelationshipDTO> getRelationship(
            @RequestHeader("token") String token,
            @PathVariable("relationshipId") String relationshipId) {
        try {
            String userId = authService.getUserIdFromToken(token);
            authService.validateUser(token, userId);
            
            UserSchoolRelationshipDTO relationship = relationshipService.getRelationshipById(relationshipId);
            return ResponseEntity.ok(relationship);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<UserSchoolRelationshipDTO>> getUserRelationships(
            @RequestHeader("token") String token,
            @PathVariable("userId") String userId) {
        try {
            String viewerId = authService.getUserIdFromToken(token);
            authService.validateUser(token, viewerId);
            
            List<UserSchoolRelationshipDTO> relationships = relationshipService.getUserRelationships(userId);
            return ResponseEntity.ok(relationships);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/school/{schoolId}")
    public ResponseEntity<List<UserSchoolRelationshipDTO>> getSchoolRelationships(
            @RequestHeader("token") String token,
            @PathVariable("schoolId") String schoolId) {
        try {
            String userId = authService.getUserIdFromToken(token);
            authService.validateUser(token, userId);
            
            List<UserSchoolRelationshipDTO> relationships = relationshipService.getSchoolRelationships(schoolId);
            return ResponseEntity.ok(relationships);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/user/{userId}/current")
    public ResponseEntity<UserSchoolRelationshipDTO> getCurrentUserRelationship(
            @RequestHeader("token") String token,
            @PathVariable("userId") String userId) {
        try {
            authService.validateUser(token, userId);
            
            UserSchoolRelationshipDTO relationship = relationshipService.getCurrentUserRelationship(userId);
            return ResponseEntity.ok(relationship);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/user/{userId}/role/{role}")
    public ResponseEntity<List<UserSchoolRelationshipDTO>> getUserRelationshipsByRole(
            @RequestHeader("token") String token,
            @PathVariable("userId") String userId,
            @PathVariable("role") String role) {
        try {
            authService.validateUser(token, userId);
            
            UserSchoolRole userSchoolRole = UserSchoolRole.valueOf(role.toUpperCase());
            List<UserSchoolRelationshipDTO> relationships = relationshipService.getUserRelationshipsByRole(userId, userSchoolRole);
            return ResponseEntity.ok(relationships);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/school/{schoolId}/role/{role}")
    public ResponseEntity<List<UserSchoolRelationshipDTO>> getSchoolRelationshipsByRole(
            @RequestHeader("token") String token,
            @PathVariable("schoolId") String schoolId,
            @PathVariable("role") String role) {
        try {
            String userId = authService.getUserIdFromToken(token);
            authService.validateUser(token, userId);
            
            UserSchoolRole userSchoolRole = UserSchoolRole.valueOf(role.toUpperCase());
            List<UserSchoolRelationshipDTO> relationships = relationshipService.getSchoolRelationshipsByRole(schoolId, userSchoolRole);
            return ResponseEntity.ok(relationships);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/transfer/{userId}/{newSchoolId}/{role}")
    public ResponseEntity<?> transferUserToNewSchool(
            @RequestHeader("token") String token,
            @PathVariable("userId") String userId,
            @PathVariable("newSchoolId") String newSchoolId,
            @PathVariable("role") String role) {
        try {
            authService.validateUser(token, userId);
            
            UserSchoolRole userSchoolRole = UserSchoolRole.valueOf(role.toUpperCase());
            relationshipService.transferUserToNewSchool(userId, newSchoolId, userSchoolRole);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/graduate/{userId}")
    public ResponseEntity<?> graduateUser(
            @RequestHeader("token") String token,
            @PathVariable("userId") String userId) {
        try {
            authService.validateUser(token, userId);
            
            relationshipService.graduateUser(userId);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/school/{schoolId}/count/{role}/{status}")
    public ResponseEntity<Integer> getSchoolUserCount(
            @RequestHeader("token") String token,
            @PathVariable("schoolId") String schoolId,
            @PathVariable("role") String role,
            @PathVariable("status") String status) {
        try {
            String userId = authService.getUserIdFromToken(token);
            authService.validateUser(token, userId);
            
            UserSchoolRole userSchoolRole = UserSchoolRole.valueOf(role.toUpperCase());
            RelationshipStatus relationshipStatus = RelationshipStatus.valueOf(status.toUpperCase());
            int count = relationshipService.getSchoolUserCount(schoolId, userSchoolRole, relationshipStatus);
            return ResponseEntity.ok(count);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 