package com.admin.school.services;

import com.admin.school.models.Post;
import com.admin.school.models.User;
import com.admin.school.dto.post.FeedPostDTO;

import java.util.Date;
import java.util.List;

public interface UserService {
    void connectWithUser(String userId, String authorId);

    void disconnectFromUser(String authorId, String userId);

    List<User> getConnections(String userId);

    void followOrg(String authorId, String orgId);

    void unfollowOrg(String authorId, String userId);

    List<Post> getFeed(String userId);
    
    List<FeedPostDTO> getEnhancedFeed(String userId);
    
    List<FeedPostDTO> getFeedWithPagination(String userId, int page, int size);
    
    List<FeedPostDTO> getFeedSinceDate(String userId, Date sinceDate);
    
    boolean areUsersConnected(String user1Id, String user2Id);

    void acceptConnectionRequest(String authorId, String userId);
}
