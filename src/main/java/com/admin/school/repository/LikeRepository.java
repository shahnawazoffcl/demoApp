package com.admin.school.repository;

import com.admin.school.models.PostLike;
import com.admin.school.models.Post;
import com.admin.school.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LikeRepository extends JpaRepository<PostLike, UUID> {
    PostLike findByUserAndPost(User user, Post post);

    int countByPostAndLiked(Post post, boolean liked);
}
