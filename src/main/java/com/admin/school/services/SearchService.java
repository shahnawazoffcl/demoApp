package com.admin.school.services;

import com.admin.school.dto.search.SearchRequestDTO;
import com.admin.school.dto.search.SearchResultDTO;

public interface SearchService {
    SearchResultDTO search(SearchRequestDTO searchRequest, String currentUserId);
    
    SearchResultDTO searchUsers(String query, String currentUserId, int page, int size);
    
    SearchResultDTO searchOrganizations(String query, String currentUserId, int page, int size);
    
    SearchResultDTO getSearchSuggestions(String query, String currentUserId);
} 