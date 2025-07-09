package com.admin.school.repository;

import com.admin.school.models.OrgSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrgSessionRepository extends JpaRepository<OrgSession, UUID> {
    Optional<OrgSession> findByToken(String token);
}
