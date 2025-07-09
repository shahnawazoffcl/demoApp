package com.admin.school.dto.search;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SearchRequestDTO {
    private String query; // Search term
    private String type; // "users", "organizations", "all"
    private String role; // Filter by user role (student, teacher, etc.)
    private String location; // Filter by location
    private int page = 0;
    private int size = 10;
    private String sortBy = "relevance"; // "relevance", "name", "connections"
} 