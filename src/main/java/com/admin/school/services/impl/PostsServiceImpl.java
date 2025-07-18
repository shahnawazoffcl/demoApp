package com.admin.school.services.impl;

import com.admin.school.dto.post.PostsProcessDTO;
import com.admin.school.exception.PostNotFoundException;
import com.admin.school.exception.UserNotFoundException;
import com.admin.school.models.*;
import com.admin.school.repository.LikeRepository;
import com.admin.school.repository.OrganizationRepository;
import com.admin.school.repository.PostsRepository;
import com.admin.school.repository.UserRepository;
import com.admin.school.services.NotificationService;
import com.admin.school.services.PostsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
public class PostsServiceImpl implements PostsService {

    private final PostsRepository postsRepository;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final LikeRepository likeRepository;
    private final NotificationService notificationService;

    public PostsServiceImpl(PostsRepository postsRepository, UserRepository userRepository, OrganizationRepository organizationRepository, LikeRepository likeRepository, NotificationService notificationService) {
        this.postsRepository = postsRepository;
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.likeRepository = likeRepository;
        this.notificationService = notificationService;
    }

    @Override
    public Post createPost(Post post, String author, String authorId) {
        if(author.equals(AuthorType.USER.toString())){
            Optional<User> user = userRepository.findById(UUID.fromString(authorId));
            if (user.isEmpty()) {
                throw new UserNotFoundException("User not found");
            }
            post.setUser(user.get());
        }
        else if(author.equals(AuthorType.ORGANIZATION.toString())){
            Optional<Organization> org = organizationRepository.findById(UUID.fromString(authorId));
            if (org.isEmpty()) {
                throw new UserNotFoundException("Organization not found");
            }
            post.setOrganization(org.get());
        }
        post.setComments(new ArrayList<>());
        post.setLikes(new ArrayList<>());
        post.setCreatedAt(new Date());
        return postsRepository.save(post);
    }

    @Override
    public Post getPost(String id) {
        try {
            Optional<Post> optionalPost = postsRepository.findById(UUID.fromString(id));
            if (optionalPost.isPresent()) {
                return optionalPost.get();
            }
            throw new PostNotFoundException("Post not found");
        } catch (Exception e) {
            log.error("Error: ", e);
            throw e;
        }
    }

    @Override
    @Transactional
    public void likePost(String id, String userId) {
        User user = userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new RuntimeException("User not found"));
        Post post = getPost(id);

        PostLike existingLike = likeRepository.findByUserAndPost(user, post);

        if (existingLike != null) {
            existingLike.setLiked(!existingLike.isLiked());
            if (!post.getUser().getId().equals(user.getId())) {
                if (existingLike.isLiked())
                    notificationService.sendPostLikeNotification(post, user);
                else
                    notificationService.deletePostLikeNotification(post, user);
            }
            likeRepository.save(existingLike);
        } else {
            PostLike newLike = new PostLike();
            newLike.setUser(user);
            newLike.setPost(post);
            newLike.setLiked(true);
            if (!post.getUser().getId().equals(user.getId())) {
                notificationService.sendPostLikeNotification(post, user);
                likeRepository.save(newLike);
            }
        }
    }

    @Override
    public List<PostsProcessDTO> getAllPostsForUser(String userId) {
        userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new UserNotFoundException("User not found"));
        List<Object[]> lkePosts= postsRepository.findPostsWithLikeStatusByUserId(UUID.fromString(userId));
        List<PostsProcessDTO> postsProcessDTOS = new ArrayList<>();
        for(Object[] obj: lkePosts){
            Post post = (Post) obj[0];
            Boolean liked = (Boolean) obj[1];
            if(liked == null){
                liked = false;
            }
            PostsProcessDTO postsProcessDTO = new PostsProcessDTO(post, liked);
            postsProcessDTOS.add(postsProcessDTO);
        }
        return postsProcessDTOS;
    }


}
