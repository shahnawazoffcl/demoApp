package com.admin.school.dto.user;

import lombok.Getter;
import lombok.Setter;
import java.util.Date;
import java.util.UUID;

@Getter
@Setter
public class UserResponseDTO {

    private UUID id;
    private String email;
    private String username;
    private String profilePictureUrl;
    private String role;
    private Date createdAt;
    private Integer connectionsCount;
    private String profileComplete;

}
