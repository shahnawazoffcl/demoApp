package com.admin.school.repository;

import com.admin.school.models.Post;
import com.admin.school.models.PostReport;
import com.admin.school.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostReportRepository extends JpaRepository<PostReport, java.util.UUID> {
    Optional<PostReport> findByPostAndReportedBy(Post post, User reportedBy);
    long countByPost(Post post);
} 