package com.admin.school.controllers;

import com.admin.school.dto.search.SearchRequestDTO;
import com.admin.school.dto.search.SearchResultDTO;
import com.admin.school.services.AuthService;
import com.admin.school.services.SearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/search")
public class SearchController {

    private final SearchService searchService;
    private final AuthService authService;

    public SearchController(SearchService searchService, AuthService authService) {
        this.searchService = searchService;
        this.authService = authService;
    }

    @PostMapping("/")
    public ResponseEntity<SearchResultDTO> search(@RequestHeader("token") String token, 
                                                 @RequestBody SearchRequestDTO searchRequest) {
        String userId = authService.getUserIdFromToken(token);
        authService.validateUser(token, userId);
        
        SearchResultDTO result = searchService.search(searchRequest, userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/users")
    public ResponseEntity<SearchResultDTO> searchUsers(@RequestHeader("token") String token,
                                                      @RequestParam String query,
                                                      @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "10") int size) {
        String userId = authService.getUserIdFromToken(token);
        authService.validateUser(token, userId);
        
        SearchResultDTO result = searchService.searchUsers(query, userId, page, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/organizations")
    public ResponseEntity<SearchResultDTO> searchOrganizations(@RequestHeader("token") String token,
                                                              @RequestParam String query,
                                                              @RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "10") int size) {
        String userId = authService.getUserIdFromToken(token);
        authService.validateUser(token, userId);
        
        SearchResultDTO result = searchService.searchOrganizations(query, userId, page, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/suggestions")
    public ResponseEntity<SearchResultDTO> getSearchSuggestions(@RequestHeader("token") String token,
                                                               @RequestParam String query) {
        String userId = authService.getUserIdFromToken(token);
        authService.validateUser(token, userId);
        
        SearchResultDTO result = searchService.getSearchSuggestions(query, userId);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/quick")
    public ResponseEntity<SearchResultDTO> quickSearch(@RequestHeader("token") String token,
                                                      @RequestParam String query) {
        String userId = authService.getUserIdFromToken(token);
        authService.validateUser(token, userId);
        
        SearchRequestDTO searchRequest = new SearchRequestDTO();
        searchRequest.setQuery(query);
        searchRequest.setType("all");
        searchRequest.setPage(0);
        searchRequest.setSize(5);
        
        SearchResultDTO result = searchService.search(searchRequest, userId);
        return ResponseEntity.ok(result);
    }
} 