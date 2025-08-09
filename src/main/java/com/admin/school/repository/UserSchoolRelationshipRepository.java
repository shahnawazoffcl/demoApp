package com.admin.school.repository;

import com.admin.school.models.UserSchoolRelationship;
import com.admin.school.models.UserSchoolRole;
import com.admin.school.models.RelationshipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserSchoolRelationshipRepository extends JpaRepository<UserSchoolRelationship, UUID> {

    @Query("SELECT usr FROM UserSchoolRelationship usr WHERE usr.user.id = :userId ORDER BY usr.startDate DESC")
    List<UserSchoolRelationship> findByUserIdOrderByStartDateDesc(@Param("userId") UUID userId);

    @Query("SELECT usr FROM UserSchoolRelationship usr WHERE usr.school.id = :schoolId ORDER BY usr.startDate DESC")
    List<UserSchoolRelationship> findBySchoolIdOrderByStartDateDesc(@Param("schoolId") UUID schoolId);

    @Query("SELECT usr FROM UserSchoolRelationship usr WHERE usr.user.id = :userId AND usr.isCurrent = true")
    Optional<UserSchoolRelationship> findCurrentByUserId(@Param("userId") UUID userId);

    @Query("SELECT usr FROM UserSchoolRelationship usr WHERE usr.user.id = :userId AND usr.status = :status")
    List<UserSchoolRelationship> findByUserIdAndStatus(@Param("userId") UUID userId, @Param("status") RelationshipStatus status);

    @Query("SELECT usr FROM UserSchoolRelationship usr WHERE usr.school.id = :schoolId AND usr.role = :role AND usr.status = :status")
    List<UserSchoolRelationship> findBySchoolIdAndRoleAndStatus(@Param("schoolId") UUID schoolId, @Param("role") UserSchoolRole role, @Param("status") RelationshipStatus status);

    @Query("SELECT usr FROM UserSchoolRelationship usr WHERE usr.user.id = :userId AND usr.role = :role")
    List<UserSchoolRelationship> findByUserIdAndRole(@Param("userId") UUID userId, @Param("role") UserSchoolRole role);

    @Query("SELECT COUNT(usr) FROM UserSchoolRelationship usr WHERE usr.school.id = :schoolId AND usr.role = :role AND usr.status = :status")
    int countBySchoolIdAndRoleAndStatus(@Param("schoolId") UUID schoolId, @Param("role") UserSchoolRole role, @Param("status") RelationshipStatus status);

    boolean existsByUserIdAndSchoolIdAndStatus(UUID userId, UUID schoolId, RelationshipStatus status);
} 