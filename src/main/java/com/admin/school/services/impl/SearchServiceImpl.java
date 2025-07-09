package com.admin.school.services.impl;

import com.admin.school.dto.search.SearchRequestDTO;
import com.admin.school.dto.search.SearchResultDTO;
import com.admin.school.dto.search.UserSearchResult;
import com.admin.school.dto.search.OrganizationSearchResult;
import com.admin.school.models.User;
import com.admin.school.models.Organization;
import com.admin.school.repository.UserRepository;
import com.admin.school.repository.OrganizationRepository;
import com.admin.school.services.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class SearchServiceImpl implements SearchService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;

    public SearchServiceImpl(UserRepository userRepository, OrganizationRepository organizationRepository) {
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
    }

    @Override
    public SearchResultDTO search(SearchRequestDTO searchRequest, String currentUserId) {
        log.info("Searching for: {} with type: {}", searchRequest.getQuery(), searchRequest.getType());
        
        SearchResultDTO result = new SearchResultDTO();
        result.setQuery(searchRequest.getQuery());
        result.setType(searchRequest.getType());
        
        if ("users".equals(searchRequest.getType()) || "all".equals(searchRequest.getType())) {
            SearchResultDTO userResults = searchUsers(searchRequest.getQuery(), currentUserId, searchRequest.getPage(), searchRequest.getSize());
            result.setUsers(userResults.getUsers());
            result.setTotalUsers(userResults.getTotalUsers());
        }
        
        if ("organizations".equals(searchRequest.getType()) || "all".equals(searchRequest.getType())) {
            SearchResultDTO orgResults = searchOrganizations(searchRequest.getQuery(), currentUserId, searchRequest.getPage(), searchRequest.getSize());
            result.setOrganizations(orgResults.getOrganizations());
            result.setTotalOrganizations(orgResults.getTotalOrganizations());
        }
        
        result.setTotalResults(result.getTotalUsers() + result.getTotalOrganizations());
        result.setCurrentPage(searchRequest.getPage());
        result.setTotalPages((int) Math.ceil((double) result.getTotalResults() / searchRequest.getSize()));
        
        return result;
    }

    @Override
    public SearchResultDTO searchUsers(String query, String currentUserId, int page, int size) {
        log.info("Searching users with query: {}", query);
        
        Pageable pageable = PageRequest.of(page, size);
        List<User> users = userRepository.searchUsersWithPagination(query, pageable);
        
        SearchResultDTO result = new SearchResultDTO();
        result.setUsers(convertToUserSearchResults(users, currentUserId));
        result.setTotalUsers(users.size());
        result.setCurrentPage(page);
        
        return result;
    }

    @Override
    public SearchResultDTO searchOrganizations(String query, String currentUserId, int page, int size) {
        log.info("Searching organizations with query: {}", query);
        
        Pageable pageable = PageRequest.of(page, size);
        List<Organization> organizations = organizationRepository.searchOrganizationsWithPagination(query, pageable);
        
        SearchResultDTO result = new SearchResultDTO();
        result.setOrganizations(convertToOrganizationSearchResults(organizations, currentUserId));
        result.setTotalOrganizations(organizations.size());
        result.setCurrentPage(page);
        
        return result;
    }

    @Override
    public SearchResultDTO getSearchSuggestions(String query, String currentUserId) {
        log.info("Getting search suggestions for query: {}", query);
        
        // Get limited suggestions for autocomplete
        List<User> userSuggestions = userRepository.searchUsers(query).stream().limit(5).collect(Collectors.toList());
        List<Organization> orgSuggestions = organizationRepository.searchOrganizations(query).stream().limit(5).collect(Collectors.toList());
        
        SearchResultDTO result = new SearchResultDTO();
        result.setUsers(convertToUserSearchResults(userSuggestions, currentUserId));
        result.setOrganizations(convertToOrganizationSearchResults(orgSuggestions, currentUserId));
        result.setQuery(query);
        
        return result;
    }

    private List<UserSearchResult> convertToUserSearchResults(List<User> users, String currentUserId) {
        List<UserSearchResult> results = new ArrayList<>();
        User currentUser = userRepository.findById(UUID.fromString(currentUserId)).orElse(null);
        
        for (User user : users) {
            if (user.getId().toString().equals(currentUserId)) {
                continue; // Skip current user
            }
            
            UserSearchResult result = new UserSearchResult();
            result.setId(user.getId());
            result.setUsername(user.getUsername());
            result.setEmail(user.getEmail());
            result.setRole(user.getRole());
            result.setProfilePictureUrl(user.getProfilePictureUrl());
            
            // Check if already connected
            if (currentUser != null && currentUser.getConnections() != null) {
                result.setIsConnected(currentUser.getConnections().contains(user));
            }
            
            // Calculate mutual connections
            if (currentUser != null && currentUser.getConnections() != null && user.getConnections() != null) {
                int mutualConnections = (int) currentUser.getConnections().stream()
                    .filter(user.getConnections()::contains)
                    .count();
                result.setMutualConnections(mutualConnections);
            }
            
            // Set relevance score based on mutual connections
            if (result.getMutualConnections() > 5) {
                result.setRelevanceScore("high");
            } else if (result.getMutualConnections() > 2) {
                result.setRelevanceScore("medium");
            } else {
                result.setRelevanceScore("low");
            }
            
            result.setCanConnect(!result.isConnected());
            results.add(result);
        }
        
        return results;
    }

    private List<OrganizationSearchResult> convertToOrganizationSearchResults(List<Organization> organizations, String currentUserId) {
        List<OrganizationSearchResult> results = new ArrayList<>();
        User currentUser = userRepository.findById(UUID.fromString(currentUserId)).orElse(null);
        
        for (Organization org : organizations) {
            OrganizationSearchResult result = new OrganizationSearchResult();
            result.setId(org.getId());
            result.setName(org.getName());
            result.setEmail(org.getEmail());
            result.setAddress(org.getAddress());
            result.setPhone(org.getPhone());
            
            // Check if already following
            if (currentUser != null && org.getFollowers() != null) {
                result.setIsFollowing(org.getFollowers().contains(currentUser));
            }
            
            // Set followers count
            if (org.getFollowers() != null) {
                result.setFollowersCount(org.getFollowers().size());
            }
            
            // Set relevance score based on followers count
            if (result.getFollowersCount() > 100) {
                result.setRelevanceScore("high");
            } else if (result.getFollowersCount() > 50) {
                result.setRelevanceScore("medium");
            } else {
                result.setRelevanceScore("low");
            }
            
            result.setCanFollow(!result.isFollowing());
            results.add(result);
        }
        
        return results;
    }
} 