package com.admin.school.dto.search;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class SearchResultDTO {
    private List<UserSearchResult> users;
    private List<OrganizationSearchResult> organizations;
    private int totalUsers;
    private int totalOrganizations;
    private int totalResults;
    private int currentPage;
    private int totalPages;
    private String query;
    private String type;
}

 