package com.admin.school.repository;

import com.admin.school.models.Organization;
import com.admin.school.models.Post;
import com.admin.school.models.PostMention;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PostMentionRepository extends JpaRepository<PostMention, UUID> {
    List<PostMention> findByOrganizationOrderByCreatedAtDesc(Organization organization);
    List<PostMention> findByPost(Post post);
} 