package com.admin.school.controllers;

import com.admin.school.dto.post.PostResponseDTO;
import com.admin.school.dto.search.OrganizationSearchResult;
import com.admin.school.dto.post.FeedPostDTO;
import com.admin.school.dto.organization.OrganizationResponseDTO;
import com.admin.school.dto.organization.OrganizationProfileDTO;
import com.admin.school.models.Organization;
import com.admin.school.models.Post;
import com.admin.school.models.PostMention;
import com.admin.school.models.User;
import com.admin.school.repository.OrganizationRepository;
import com.admin.school.repository.PostMentionRepository;
import com.admin.school.repository.PostsRepository;
import com.admin.school.services.AuthService;
import com.admin.school.services.UserSchoolRelationshipService;
import com.admin.school.services.PostsService;
import com.admin.school.services.OrganizationService;
import com.admin.school.services.UserService;
import com.admin.school.controllers.utils.PostControllerUtils;
import com.admin.school.controllers.utils.OrganizationControllerUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import com.admin.school.models.UserSchoolRelationship;

@RestController
@RequestMapping("/organizations")
public class OrganizationController {

    private final UserSchoolRelationshipService userSchoolRelationshipService;
    private final AuthService authService;
    private final PostsService postsService;
    private final OrganizationService organizationService;
    private final PostMentionRepository postMentionRepository;
    private final PostsRepository postsRepository;
    private final UserService userService;

    public OrganizationController(UserSchoolRelationshipService userSchoolRelationshipService, AuthService authService, PostsService postsService, OrganizationService organizationService, PostMentionRepository postMentionRepository, PostsRepository postsRepository, UserService userService) {
        this.userSchoolRelationshipService = userSchoolRelationshipService;
        this.authService = authService;
        this.postsService = postsService;
        this.organizationService = organizationService;
        this.postMentionRepository = postMentionRepository;
        this.postsRepository = postsRepository;
        this.userService = userService;
    }

    // Get all countries for dropdown
    @GetMapping("/countries")
    public ResponseEntity<List<String>> getAllCountries(@RequestHeader("token") String token) {
        try {
            String userId = authService.getUserIdFromToken(token);
            authService.validateUser(token, userId);
            
            List<String> countries = userSchoolRelationshipService.getAllCountries();
            return ResponseEntity.ok(countries);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Get states by country for dropdown
    @GetMapping("/states")
    public ResponseEntity<List<String>> getStatesByCountry(
            @RequestHeader("token") String token,
            @RequestParam String country) {
        try {
            String userId = authService.getUserIdFromToken(token);
            authService.validateUser(token, userId);
            
            List<String> states = userSchoolRelationshipService.getAllStatesByCountry(country);
            return ResponseEntity.ok(states);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Get cities by country and state for dropdown
    @GetMapping("/cities")
    public ResponseEntity<List<String>> getCitiesByCountryAndState(
            @RequestHeader("token") String token,
            @RequestParam String country,
            @RequestParam String state) {
        try {
            String userId = authService.getUserIdFromToken(token);
            authService.validateUser(token, userId);
            
            List<String> cities = userSchoolRelationshipService.getAllCitiesByState(state);
            return ResponseEntity.ok(cities);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Search organizations by location and name
    @GetMapping("/search")
    public ResponseEntity<List<OrganizationSearchResult>> searchOrganizationsByLocation(
            @RequestHeader("token") String token,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String state,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String name) {
        try {
            String userId = authService.getUserIdFromToken(token);
            authService.validateUser(token, userId);
            
            List<Organization> organizations;
                         // Search by all criteria
            organizations = userSchoolRelationshipService.getByCountryAndStateAndCityAndNameContainingIgnoreCase(country, state, city, name);

            
            List<OrganizationSearchResult> results = organizations.stream()
                .map(this::convertToSearchResult)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(results);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Get all staff (teachers, staff) for an organization
    @GetMapping("/{organizationId}/staff")
    public ResponseEntity<List<UserDTO>> getOrganizationStaff(
            @RequestHeader("token") String token,
            @PathVariable("organizationId") String organizationId) {
        try {
            String userId = authService.getUserIdFromToken(token);
            authService.validateUser(token, userId);
            List<User> staff = userSchoolRelationshipService.getOrganizationStaff(organizationId);
            List<UserDTO> staffDTOs = staff.stream().map(UserDTO::fromUser).collect(Collectors.toList());
            return ResponseEntity.ok(staffDTOs);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Get organization feed
    @GetMapping("/{organizationId}/feed")
    public ResponseEntity<List<FeedPostDTO>> getOrganizationFeed(
            @RequestHeader("token") String token,
            @PathVariable("organizationId") String organizationId) {
        try {
            authService.validateOrg(token, organizationId);
            List<FeedPostDTO> feed = postsService.getOrganizationFeed(organizationId);
            return ResponseEntity.ok(feed);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // Complete organization profile
    @PostMapping("/complete-profile/{organizationId}")
    public ResponseEntity<OrganizationResponseDTO> completeOrganizationProfile(
            @RequestHeader("token") String token,
            @PathVariable("organizationId") String organizationId,
            @RequestBody OrganizationProfileDTO profileDTO) {
        try {
            authService.validateOrg(token, organizationId);
            Organization updatedOrganization = organizationService.completeProfile(organizationId, profileDTO);
            OrganizationResponseDTO responseDTO = OrganizationControllerUtils.mapOrganizationToOrganizationResponseDTO(updatedOrganization);
            return ResponseEntity.ok(responseDTO);
        } catch (Exception e) {
            e.printStackTrace(); // Add logging to see the actual error
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping("/{orgId}/mentions")
    public ResponseEntity<List<PostResponseDTO>> getMentions(
            @RequestHeader("token") String token,
            @PathVariable("orgId") String orgId) {
        try {
            String userId = authService.getUserIdFromToken(token);
            authService.validateUser(token, userId);
            UUID oid = UUID.fromString(orgId);
            Organization org = organizationService.getOrganizationById(oid);
            List<PostMention> mentions = postMentionRepository.findByOrganizationOrderByCreatedAtDesc(org);
            List<PostResponseDTO> dtos = new ArrayList<>();
            for (PostMention m : mentions) {
                Post p = m.getPost();
                dtos.add(PostControllerUtils.mapPostToPostResponseDTO(p, userId, userService));
            }
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{orgId}/own-posts")
    public ResponseEntity<List<PostResponseDTO>> getOwnPosts(
            @RequestHeader("token") String token,
            @PathVariable("orgId") String orgId) {
        try {
            String userId = authService.getUserIdFromToken(token);
            authService.validateUser(token, userId);
            UUID oid = UUID.fromString(orgId);
            Organization org = organizationService.getOrganizationById(oid);
            List<Post> ownPosts = postsRepository.findByOrganizationOrderByCreatedAtDesc(org);
            List<PostResponseDTO> dtos = new ArrayList<>();
            for (Post p : ownPosts) {
                dtos.add(PostControllerUtils.mapPostToPostResponseDTO(p, userId, userService));
            }
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{orgId}/employee-posts")
    public ResponseEntity<List<PostResponseDTO>> getEmployeePosts(
            @RequestHeader("token") String token,
            @PathVariable("orgId") String orgId) {
        try {
            String userId = authService.getUserIdFromToken(token);
            authService.validateUser(token, userId);
            UUID oid = UUID.fromString(orgId);
            Organization org = organizationService.getOrganizationById(oid);
            List<com.admin.school.dto.user.UserSchoolRelationshipDTO> relationships = userSchoolRelationshipService.getUserRelationships(userId);
            List<Post> employeePosts = new ArrayList<>();
            
            // Get posts from users who have active relationships with this org
            for (com.admin.school.dto.user.UserSchoolRelationshipDTO rel : relationships) {
                if (rel.getSchoolId().equals(orgId) && rel.getStatus().equals("ACTIVE")) {
                    // Find user by ID from the relationship
                    User user = userService.getUserById(rel.getUserId());
                    List<Post> userPosts = postsRepository.findByUserOrderByCreatedAtDesc(user);
                    employeePosts.addAll(userPosts);
                }
            }
            
            // Sort by creation date (newest first)
            employeePosts.sort((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()));
            
            List<PostResponseDTO> dtos = new ArrayList<>();
            for (Post p : employeePosts) {
                dtos.add(PostControllerUtils.mapPostToPostResponseDTO(p, userId, userService));
            }
            return ResponseEntity.ok(dtos);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    private OrganizationSearchResult convertToSearchResult(Organization org) {
        OrganizationSearchResult result = new OrganizationSearchResult();
        result.setId(org.getId());
        result.setName(org.getName());
        result.setEmail(org.getEmail());
        result.setAddress(org.getAddress());
        result.setPhone(org.getPhone());
        result.setFollowersCount(org.getFollowers() != null ? org.getFollowers().size() : 0);
        result.setIsFollowing(false); // Default value, can be enhanced later
        result.setCanFollow(true);
        result.setRelevanceScore("medium"); // Default value
        return result;
    }
} 

// DTO for returning user info
class UserDTO {
    public String id;
    public String username;
    public String email;
    public String role;
    public String profilePictureUrl;

    public static UserDTO fromUser(com.admin.school.models.User user) {
        UserDTO dto = new UserDTO();
        dto.id = user.getId().toString();
        dto.username = user.getUsername();
        dto.email = user.getEmail();
        dto.role = user.getRole();
        dto.profilePictureUrl = user.getProfilePictureUrl();
        return dto;
    }
}