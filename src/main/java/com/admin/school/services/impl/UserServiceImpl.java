package com.admin.school.services.impl;

import com.admin.school.controllers.utils.PostControllerUtils;
import com.admin.school.dto.post.FeedPostDTO;
import com.admin.school.exception.UserNotFoundException;
import com.admin.school.models.Organization;
import com.admin.school.models.Post;
import com.admin.school.models.PostLike;
import com.admin.school.models.User;
import com.admin.school.repository.LikeRepository;
import com.admin.school.repository.OrganizationRepository;
import com.admin.school.repository.PostsRepository;
import com.admin.school.repository.UserRepository;
import com.admin.school.services.NotificationService;
import com.admin.school.services.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import com.admin.school.dto.user.CompleteProfileDTO;


@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final PostsRepository postsRepository;
    private final LikeRepository likeRepository;
    private final NotificationService notificationService;

    public UserServiceImpl(UserRepository userRepository, OrganizationRepository organizationRepository, PostsRepository postsRepository, LikeRepository likeRepository, NotificationService notificationService) {
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.postsRepository = postsRepository;
        this.likeRepository = likeRepository;
        this.notificationService = notificationService;
    }

    @Override
    public void connectWithUser(String authorId, String userId) {
        log.info("User with id: {} is connecting with user with id: {}", authorId, userId);
        User user1 = userRepository.findById(UUID.fromString(authorId)).orElseThrow(() -> new UserNotFoundException("User not found"));
        User user2 = userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user2.getConnections().contains(user1)) {
            log.error("Users with id: {} and {} are already connected", userId, authorId);
            return;
        }

        notificationService.sendConnectionRequestNotification(user1, user2);
    }

    @Override
    public void disconnectFromUser(String authorId, String userId) {
        log.info("User with id: {} is disconnecting from user with id: {}", userId, authorId);
        User user1 = userRepository.findById(UUID.fromString(authorId)).orElseThrow(() -> new UserNotFoundException("User not found"));
        User user2 = userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new UserNotFoundException("User not found"));
        user1.getConnections().remove(user2);
        user2.getConnections().remove(user1);
        userRepository.save(user1);
        userRepository.save(user2);
    }

    @Override
    public List<User> getConnections(String userId) {
        log.info("Getting connections for user with id: {}", userId);
        User user = userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new UserNotFoundException("User not found"));
        return user.getConnections();
    }

    @Override
    public void followOrg(String authorId, String orgId) {
        log.info("User with id: {} is following Organization with id: {}", authorId, orgId);
        Organization org = organizationRepository.findById(UUID.fromString(orgId)).orElseThrow(() -> new UserNotFoundException("Organization not found"));
        User follower = userRepository.findById(UUID.fromString(authorId)).orElseThrow(() -> new UserNotFoundException("Follower not found"));

        if (org.getFollowers().contains(follower)) {
            log.error("User with id: {} already following with org with id: {}", authorId, orgId);
            return;
        }
        org.getFollowers().add(follower);
        organizationRepository.save(org);

    }

    @Override
    public void unfollowOrg(String authorId, String orgId) {
        log.info("User with id: {} is unfollowing org with id: {}", orgId, authorId);
        User follower = userRepository.findById(UUID.fromString(authorId)).orElseThrow(() -> new UserNotFoundException("Follower not found"));
        Organization org = organizationRepository.findById(UUID.fromString(orgId)).orElseThrow(() -> new UserNotFoundException("Organization not found"));
        org.getFollowers().remove(follower);
        organizationRepository.save(org);
    }

    @Override
    public List<Post> getFeed(String userId) {
        log.info("Getting feed for user with id: {}", userId);
        User user = userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new UserNotFoundException("User not found"));
        List<Post> feed = postsRepository.getFeed(user.getId());
        
        // Sort by relevance score (calculated in PostControllerUtils)
        feed.sort((p1, p2) -> {
            int score1 = calculateRelevanceScore(p1, user.getId());
            int score2 = calculateRelevanceScore(p2, user.getId());
            if (score1 != score2) {
                return Integer.compare(score2, score1); // Higher score first
            }
            
            // Handle null createdAt dates
            Date date1 = p1.getCreatedAt();
            Date date2 = p2.getCreatedAt();
            
            if (date1 == null && date2 == null) {
                return 0; // Both null, consider equal
            } else if (date1 == null) {
                return 1; // p1 is null, put it after p2
            } else if (date2 == null) {
                return -1; // p2 is null, put it after p1
            } else {
                return date2.compareTo(date1); // Newer posts first
            }
        });
        
        return feed;
    }

    @Override
    public List<FeedPostDTO> getEnhancedFeed(String userId) {
        log.info("Getting enhanced feed for user with id: {}", userId);
        User user = userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new UserNotFoundException("User not found"));
        List<Post> feed = postsRepository.getFeed(user.getId());
        
        List<FeedPostDTO> enhancedFeed = new ArrayList<>();
        for (Post post : feed) {
            // Calculate relevance score based on relationship
            int relevanceScore = calculateRelevanceScore(post, user.getId());
            boolean isLiked = isPostLikedByUser(post, user);

            enhancedFeed.add(PostControllerUtils.mapPostToFeedPostDTO(post, isLiked, relevanceScore, getActualLikesCount(post)));
        }
        
        return enhancedFeed;
    }

    public int getActualLikesCount(Post post) {
        int likesCount = likeRepository.countByPostAndLiked(post, true);
        return likesCount;
    }

    @Override
    public List<FeedPostDTO> getFeedWithPagination(String userId, int page, int size) {
        log.info("Getting paginated feed for user with id: {}, page: {}, size: {}", userId, page, size);
        User user = userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new UserNotFoundException("User not found"));
        
        int offset = page * size;
        List<Object[]> feedData = postsRepository.getFeedWithPagination(user.getId(), size, offset);
        
        List<FeedPostDTO> enhancedFeed = new ArrayList<>();
        for (Object[] data : feedData) {
            Post post = (Post) data[0];
            Boolean isLiked = (Boolean) data[1];
            Integer relevanceScore = (Integer) data[2];
            Long likesCount = (Long) data[3];
            Long commentsCount = (Long) data[4];
            
            FeedPostDTO feedPostDTO = PostControllerUtils.mapPostToFeedPostDTO(post, isLiked != null && isLiked, relevanceScore != null ? relevanceScore : 0, getActualLikesCount(post));
            feedPostDTO.setLikesCount(likesCount != null ? likesCount.intValue() : 0);
            feedPostDTO.setCommentsCount(commentsCount != null ? commentsCount.intValue() : 0);
            enhancedFeed.add(feedPostDTO);
        }
        
        return enhancedFeed;
    }

    @Override
    public List<FeedPostDTO> getFeedSinceDate(String userId, Date sinceDate) {
        log.info("Getting feed since date for user with id: {}, since: {}", userId, sinceDate);
        User user = userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new UserNotFoundException("User not found"));
        List<Post> feed = postsRepository.getFeedSinceDate(user.getId(), new java.sql.Timestamp(sinceDate.getTime()));
        
        List<FeedPostDTO> enhancedFeed = new ArrayList<>();
        for (Post post : feed) {
            int relevanceScore = calculateRelevanceScore(post, user.getId());
            boolean isLiked = isPostLikedByUser(post, user);
            enhancedFeed.add(PostControllerUtils.mapPostToFeedPostDTO(post, isLiked, relevanceScore, getActualLikesCount(post)));
        }
        
        return enhancedFeed;
    }

    private int calculateRelevanceScore(Post post, UUID userId) {
        if (post.getUser() != null && post.getUser().getId().equals(userId)) {
            return 3; // Own post
        } else if (post.getUser() != null && isUserConnected(userId, post.getUser().getId())) {
            return 2; // Direct connection
        } else if (post.getOrganization() != null && isUserFollowingOrg(userId, post.getOrganization().getId())) {
            return 1; // Organization following
        } else {
            return 0; // Extended network
        }
    }

    private boolean isUserConnected(UUID user1Id, UUID user2Id) {
        User user1 = userRepository.findById(user1Id).orElse(null);
        if (user1 != null && user1.getConnections() != null) {
            return user1.getConnections().stream().anyMatch(u -> u.getId().equals(user2Id));
        }
        return false;
    }

    private boolean isUserFollowingOrg(UUID userId, UUID orgId) {
        User user = userRepository.findById(userId).orElse(null);
        Organization org = organizationRepository.findById(orgId).orElse(null);
        
        if (user != null && org != null && org.getFollowers() != null) {
            return org.getFollowers().stream().anyMatch(follower -> follower.getId().equals(userId));
        }
        return false;
    }

    private boolean isPostLikedByUser(Post post, User user) {
        PostLike like = likeRepository.findByUserAndPost(user,post);
        if(like != null) {
            return like.isLiked();
        }
        return false; // If no like found, consider it not liked
    }

    @Override
    public boolean areUsersConnected(String user1Id, String user2Id) {
        log.info("Checking if users {} and {} are connected", user1Id, user2Id);
        try {
            User user1 = userRepository.findById(UUID.fromString(user1Id)).orElse(null);
            User user2 = userRepository.findById(UUID.fromString(user2Id)).orElse(null);
            
            if (user1 == null || user2 == null) {
                return false;
            }
            
            return isUserConnected(user1.getId(), user2.getId());
        } catch (Exception e) {
            log.error("Error checking user connection status", e);
            return false;
        }
    }

    @Override
    public void acceptConnectionRequest(String authorId, String userId) {
    log.info("Accepting connection request from user {} to user {}", userId, authorId);
        User user1 = userRepository.findById(UUID.fromString(authorId)).orElseThrow(() -> new UserNotFoundException("User not found"));
        User user2 = userRepository.findById(UUID.fromString(userId)).orElseThrow(() -> new UserNotFoundException("User not found"));

        user1.getConnections().add(user2);
        user2.getConnections().add(user1);
        userRepository.save(user1);
        userRepository.save(user2);
    }

    @Override
    public User completeProfile(String userId, CompleteProfileDTO completeProfileDTO) {
        log.info("Completing profile for user with id: {}", userId);
        
        User user = userRepository.findById(UUID.fromString(userId))
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        
        // Update user profile information
        if (completeProfileDTO.getPhone() != null) {
            user.setPhone(completeProfileDTO.getPhone());
        }
        
        if (completeProfileDTO.getAddress() != null) {
            user.setAddress(completeProfileDTO.getAddress());
        }
        
        if (completeProfileDTO.getProfilePicture() != null) {
            user.setProfilePictureUrl(completeProfileDTO.getProfilePicture());
        }
        
        if (completeProfileDTO.getRole() != null) {
            user.setRole(completeProfileDTO.getRole());
        }
        
        // Set profile status to COMPLETED
        user.setProfileStatus("COMPLETED");
        
        // Save the updated user
        User savedUser = userRepository.save(user);
        log.info("Profile completed successfully for user with id: {}", userId);
        
        return savedUser;
    }
}
