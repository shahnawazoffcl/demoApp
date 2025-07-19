package com.admin.school.controllers;

import com.admin.school.controllers.utils.PostControllerUtils;
import com.admin.school.controllers.utils.UserControllerUtils;
import com.admin.school.dto.post.PostResponseDTO;
import com.admin.school.dto.post.FeedPostDTO;
import com.admin.school.dto.user.UserFollowerDTO;
import com.admin.school.models.Notification;
import com.admin.school.models.Post;
import com.admin.school.models.User;
import com.admin.school.services.AuthService;
import com.admin.school.services.NotificationService;
import com.admin.school.services.PostsService;
import com.admin.school.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    private final PostsService postsService;
    private final AuthService authService;
    private final UserService userService;
    private final NotificationService notificationService;

    public UserController(PostsService postsService, AuthService authService, UserService userService, NotificationService notificationService) {
        this.postsService = postsService;
        this.authService = authService;
        this.userService = userService;
        this.notificationService = notificationService;
    }

    @GetMapping("/feed/{userId}")
    public ResponseEntity<List<PostResponseDTO>> getFeed(@RequestHeader("token") String token, @PathVariable String userId) {
        authService.validateUser(token,userId);
        List<Post> feed = userService.getFeed(userId);
        List<PostResponseDTO> feedDTOs = new ArrayList<>();
        for (Post post : feed) {
            feedDTOs.add(PostControllerUtils.mapPostToPostResponseDTO(post, userId, userService));
        }
        return ResponseEntity.ok(feedDTOs);
    }

    @GetMapping("/feed/{userId}/enhanced")
    public ResponseEntity<List<FeedPostDTO>> getEnhancedFeed(@RequestHeader("token") String token, @PathVariable String userId) {
        authService.validateUser(token,userId);
        List<FeedPostDTO> enhancedFeed = userService.getEnhancedFeed(userId);
        return ResponseEntity.ok(enhancedFeed);
    }

    @GetMapping("/feed/{userId}/paginated")
    public ResponseEntity<List<FeedPostDTO>> getFeedWithPagination(
            @RequestHeader("token") String token, 
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        authService.validateUser(token,userId);
        List<FeedPostDTO> paginatedFeed = userService.getFeedWithPagination(userId, page, size);
        return ResponseEntity.ok(paginatedFeed);
    }

    @GetMapping("/feed/{userId}/since")
    public ResponseEntity<List<FeedPostDTO>> getFeedSinceDate(
            @RequestHeader("token") String token, 
            @PathVariable String userId,
            @RequestParam String sinceDate) {
        authService.validateUser(token,userId);
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            java.util.Date date = sdf.parse(sinceDate);
            List<FeedPostDTO> feedSinceDate = userService.getFeedSinceDate(userId, date);
            return ResponseEntity.ok(feedSinceDate);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/react-post/{postId}/{userId}")
    public ResponseEntity<String> likePost(@RequestHeader("token") String token, @PathVariable("postId") String postId,@PathVariable("userId") String userId) {
        authService.validateUser(token,userId);
        postsService.likePost(postId, userId);
        return ResponseEntity.ok("Post liked successfully");
    }

    @PostMapping("connect/{authorId}/{userId}")
    public ResponseEntity<String> connectWithUser(@RequestHeader("token") String token, @PathVariable("authorId") String authorId, @PathVariable("userId") String userId) {
        authService.validateUser(token,authorId);
        userService.connectWithUser(authorId, userId);
        return ResponseEntity.ok("Users connected successfully");
    }

    @PostMapping("accept-connection/{notificationId}")
    public ResponseEntity<String> acceptConnection(@RequestHeader("token") String token, @PathVariable("notificationId") String notificationId) {
        Notification notification = notificationService.getNotificationById(notificationId);
        authService.validateUser(token, notification.getRecipient().getId().toString());
        String authorId = notification.getRecipient().getId().toString();
        String userId = notification.getSender().getId().toString();
        if (!userService.areUsersConnected(authorId, userId)) {
            userService.acceptConnectionRequest(authorId, userId);
            notificationService.deleteNotification(notificationId);
            return ResponseEntity.ok("Connection accepted successfully");
        } else {
            return ResponseEntity.badRequest().body("Users are already connected");
        }
    }

    @PostMapping("disconnect/{authorId}/{userId}")
    public ResponseEntity<String> disconnectFromUser(@RequestHeader("token") String token, @PathVariable("authorId") String authorId, @PathVariable("userId") String userId) {
        authService.validateUser(token, authorId);
        userService.disconnectFromUser(authorId, userId);
        return ResponseEntity.ok("Users disconnected successfully");
    }

    @GetMapping("get-connections/{userId}")
    public ResponseEntity<List<UserFollowerDTO>> getConnections(@RequestHeader("token") String token, @PathVariable("userId") String userId) {
        authService.validateUser(token,userId);
        List<User> connections = userService.getConnections(userId);
        List<UserFollowerDTO> connectionDTOs = new ArrayList<>();
        for (User user : connections) {
            connectionDTOs.add(UserControllerUtils.getUserFollowerDTO(user));
        }
        return ResponseEntity.ok(connectionDTOs);
    }

    @PostMapping("followOrg/{userId}/{orgId}")
    public ResponseEntity<String> followOrganization(@RequestHeader("token") String token, @PathVariable("userId") String userId, @PathVariable("orgId") String orgId) {
        authService.validateUser(token, userId);
        userService.followOrg(userId, orgId);
        return ResponseEntity.ok("Organization followed successfully");
    }

    @PostMapping("unfollowOrg/{userId}/{orgId}")
    public ResponseEntity<String> unfollowOrganization(@RequestHeader("token") String token, @PathVariable("userId") String userId, @PathVariable("orgId") String orgId) {
        authService.validateUser(token, userId);
        userService.unfollowOrg(userId, orgId);
        return ResponseEntity.ok("Organization unfollowed successfully");
    }




}
