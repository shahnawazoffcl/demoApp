package com.admin.school.repository;

import com.admin.school.models.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, UUID> {
    Optional<Organization> findByEmail(String email);
    
    @Query("SELECT o FROM Organization o WHERE " +
           "LOWER(o.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(o.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(o.address) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Organization> searchOrganizations(@Param("query") String query);
    
    @Query("SELECT o FROM Organization o WHERE " +
           "LOWER(o.name) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(o.email) LIKE LOWER(CONCAT('%', :query, '%')) OR " +
           "LOWER(o.address) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Organization> searchOrganizationsWithPagination(@Param("query") String query, org.springframework.data.domain.Pageable pageable);
}
