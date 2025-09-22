package com.admin.school.services.impl;

import com.admin.school.dto.post.PostsProcessDTO;
import com.admin.school.dto.post.FeedPostDTO;
import com.admin.school.exception.PostNotFoundException;
import com.admin.school.exception.UserNotFoundException;
import com.admin.school.models.*;
import com.admin.school.repository.LikeRepository;
import com.admin.school.repository.OrganizationRepository;
import com.admin.school.repository.PostsRepository;
import com.admin.school.repository.PostReportRepository;
import com.admin.school.repository.PostMentionRepository;
import com.admin.school.repository.UserRepository;
import com.admin.school.services.NotificationService;
import com.admin.school.services.PostsService;
import com.admin.school.services.UserSchoolRelationshipService;
import com.admin.school.controllers.utils.PostControllerUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
public class PostsServiceImpl implements PostsService {

    private final PostsRepository postsRepository;
    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final LikeRepository likeRepository;
    private final NotificationService notificationService;
    private final UserSchoolRelationshipService userSchoolRelationshipService;
    private final PostReportRepository postReportRepository;
    private final PostMentionRepository postMentionRepository;

    public PostsServiceImpl(PostsRepository postsRepository, UserRepository userRepository, OrganizationRepository organizationRepository, LikeRepository likeRepository, NotificationService notificationService, UserSchoolRelationshipService userSchoolRelationshipService, PostReportRepository postReportRepository, PostMentionRepository postMentionRepository) {
        this.postsRepository = postsRepository;
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.likeRepository = likeRepository;
        this.notificationService = notificationService;
        this.userSchoolRelationshipService = userSchoolRelationshipService;
        this.postReportRepository = postReportRepository;
        this.postMentionRepository = postMentionRepository;
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
        Post saved = postsRepository.save(post);

        // Parse mentions @OrgName and persist PostMention
        if (saved.getContent() != null) {
            java.util.regex.Matcher m = java.util.regex.Pattern.compile("@([A-Za-z0-9_][A-Za-z0-9_ ]{0,50})").matcher(saved.getContent());
            while (m.find()) {
                String orgName = m.group(1).trim();
                organizationRepository.findByName(orgName).ifPresent(org -> {
                    com.admin.school.models.PostMention mention = new com.admin.school.models.PostMention();
                    mention.setPost(saved);
                    mention.setOrganization(org);
                    postMentionRepository.save(mention);
                });
            }
        }
        return saved;
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

    @Override
    public void deletePost(String postId, String userId) {
        Optional<Post> optionalPost = postsRepository.findById(UUID.fromString(postId));
        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            // Check if the user is the author of the post
            if (!post.getUser().getId().toString().equals(userId)) {
                throw new UserNotFoundException("User is not the author of the post");
            }
            // Delete all likes associated with the post
            likeRepository.deleteAllByPost(post);
            // Delete all comments associated with the post
            post.getComments().clear();
            postsRepository.delete(post);
        } else {
            throw new PostNotFoundException("Post not found");
        }
    }

    @Override
    public List<FeedPostDTO> getOrganizationFeed(String organizationId) {
        Organization org = organizationRepository.findById(UUID.fromString(organizationId))
                .orElseThrow(() -> new UserNotFoundException("Organization not found"));

        // Get posts from multiple sources for organization feed
        List<Post> allPosts = new ArrayList<>();
        
        // 1. Organization's own posts
        List<Post> ownPosts = postsRepository.findByOrganizationOrderByCreatedAtDesc(org);
        allPosts.addAll(ownPosts);
        
        // 2. Posts from users connected to this organization (staff, students, teachers)
        List<Post> connectedUserPosts = postsRepository.findPostsByOrganizationConnections(org.getId());
        allPosts.addAll(connectedUserPosts);
        
        // 3. Posts from organizations this organization follows
        List<Post> followedOrgPosts = postsRepository.findPostsFromFollowedOrganizations(org.getId());
        allPosts.addAll(followedOrgPosts);
        
        // 4. Posts from extended network (posts liked by connected users)
        List<Post> networkPosts = postsRepository.findPostsFromOrganizationNetwork(org.getId());
        allPosts.addAll(networkPosts);

        // 5. Posts from extended network (posts liked by connected users)
        List<Post> mentionPosts = postsRepository.findMentionPostsForOrganization(org.getId());
        allPosts.addAll(mentionPosts);
        
        // Remove duplicates and sort by creation date
        allPosts = allPosts.stream()
                .distinct()
                .sorted((p1, p2) -> p2.getCreatedAt().compareTo(p1.getCreatedAt()))
                .collect(Collectors.toList());
        
        List<FeedPostDTO> feedPostDTOS = new ArrayList<>();
        for (Post post : allPosts) {
            // Calculate relevance score for organization feed
            int relevanceScore = calculateOrganizationRelevanceScore(post, org);
            boolean isLiked = isPostLikedByOrganization(post, org);
            
            FeedPostDTO feedPostDTO = PostControllerUtils.mapPostToFeedPostDTO(post, isLiked, relevanceScore, post.getLikes().size());
            feedPostDTOS.add(feedPostDTO);
        }
        return feedPostDTOS;
    }
    
    private int calculateOrganizationRelevanceScore(Post post, Organization org) {
        if (post.getOrganization() != null && post.getOrganization().getId().equals(org.getId())) {
            return 4; // Own organization post
        } else if (post.getUser() != null && isUserConnectedToOrganization(post.getUser(), org)) {
            return 3; // Connected user post
        } else if (post.getOrganization() != null && isOrganizationFollowing(post.getOrganization(), org)) {
            return 2; // Followed organization post
        } else {
            return 1; // Network post
        }
    }
    
    private boolean isUserConnectedToOrganization(User user, Organization org) {
        // Check if user has a relationship with this organization
        return userSchoolRelationshipService.hasActiveRelationship(user.getId(), org.getId());
    }
    
    private boolean isOrganizationFollowing(Organization followedOrg, Organization followerOrg) {
        // Check if followerOrg follows followedOrg
        return followedOrg.getFollowers() != null && 
               followedOrg.getFollowers().stream().anyMatch(follower -> follower.getId().equals(followerOrg.getId()));
    }
    
    private boolean isPostLikedByOrganization(Post post, Organization org) {
        // For now, return false as organizations don't like posts the same way users do
        // This can be enhanced later if needed
        return false;
    }

    @Override
    @Transactional
    public void reportPost(String postId, String reporterUserId, ReportReason reason) {
        Post post = getPost(postId);
        User reporter = userRepository.findById(UUID.fromString(reporterUserId))
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        // Prevent self-report
        if (post.getUser() != null && post.getUser().getId().equals(reporter.getId())) {
            throw new RuntimeException("Cannot report your own post");
        }

        // One report per user per post
        if (postReportRepository.findByPostAndReportedBy(post, reporter).isPresent()) {
            return; // already reported; idempotent
        }

        // Create report
        PostReport report = new PostReport();
        report.setPost(post);
        report.setReportedBy(reporter);
        report.setReason(reason);
        postReportRepository.save(report);

        // Update counters and status
        int count = (int) postReportRepository.countByPost(post);
        post.setReportCount(count);
        post.setLastReportedAt(new Date());

        // Thresholds (configurable later)
        int softThreshold = 5;
        int hardThreshold = 20;

        if (count >= hardThreshold) {
            post.setModerationStatus(PostModerationStatus.HIDDEN);
        } else if (count >= softThreshold) {
            post.setModerationStatus(PostModerationStatus.WARN);
        } else {
            post.setModerationStatus(PostModerationStatus.VISIBLE);
        }

        postsRepository.save(post);
    }
}
