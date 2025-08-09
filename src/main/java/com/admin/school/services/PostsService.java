package com.admin.school.services;

import com.admin.school.dto.post.PostsProcessDTO;
import com.admin.school.dto.post.FeedPostDTO;
import com.admin.school.models.Post;

import java.util.List;

public interface PostsService {
    Post createPost(Post post, String author, String authorId);

    Post getPost(String id);

    void likePost(String id, String userId);

    List<PostsProcessDTO> getAllPostsForUser(String userId);

    void deletePost(String postId, String userId);

    List<FeedPostDTO> getOrganizationFeed(String organizationId);
}
