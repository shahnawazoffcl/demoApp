package com.admin.school.repository;

import com.admin.school.models.Organization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.NativeQuery;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrganizationRepository extends JpaRepository<Organization, UUID> {
    Optional<Organization> findByEmail(String email);
    Optional<Organization> findByName(String name);
    
    // Location-based queries
    List<Organization> findByCountry(String country);
    List<Organization> findByCountryAndState(String country, String state);
    List<Organization> findByCountryAndStateAndCity(String country, String state, String city);
    List<Organization> findByCountryAndStateAndCityAndNameContainingIgnoreCase(String country, String state, String city, String name);
    
    // Get unique locations for dropdowns
    @NativeQuery("SELECT DISTINCT o.name FROM country o WHERE o.name IS NOT NULL ORDER BY o.name")
    List<String> findAllCountries();
    
    @NativeQuery("SELECT DISTINCT o.name FROM state o JOIN country c ON c.id = o.country_id WHERE c.name = ?1 AND o.name IS NOT NULL ORDER BY o.name")
    List<String> findAllStatesByCountry(String country);
    
    @NativeQuery("SELECT DISTINCT o.name FROM city o JOIN state s ON o.state_id = s.id WHERE s.name = ?1 AND o.name IS NOT NULL ORDER BY o.name")
    List<String> findAllCitiesByState(String state_id);
    
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
