package com.admin.school.dto.post;


import com.admin.school.dto.user.UserPostResponseDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;
import java.util.UUID;

@Setter
@Getter
public class PostResponseDTO {

    private UUID id;
    private String title;
    private String content;
    private UserPostResponseDTO author;
    private String mediaUrl;
    private String audioUrl;
    private String mediaType;
    private boolean liked;
    private int likesCount;
    private int commentsCount;
    private int sharesCount;
    private Date createdAt;
    private boolean isConnected; // Whether the current user is connected with the post author
    private boolean canConnect; // Whether the current user can connect with the post author

    // Manual getters and setters for the new fields
    public boolean isConnected() {
        return isConnected;
    }

    public void setIsConnected(boolean isConnected) {
        this.isConnected = isConnected;
    }

    public boolean isCanConnect() {
        return canConnect;
    }

    public void setCanConnect(boolean canConnect) {
        this.canConnect = canConnect;
    }
}
