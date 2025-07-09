package com.admin.school.controllers.utils;

import com.admin.school.dto.user.UserFollowerDTO;
import com.admin.school.models.User;

public class UserControllerUtils {

    public static UserFollowerDTO getUserFollowerDTO(User user) {
        UserFollowerDTO userFollowerDTO = new UserFollowerDTO();
        userFollowerDTO.setEmail(user.getEmail());
        userFollowerDTO.setId(String.valueOf(user.getId()));
        userFollowerDTO.setUsername(user.getUsername());
        userFollowerDTO.setRole(user.getRole());
        userFollowerDTO.setProfilePictureUrl(user.getProfilePictureUrl());
        return userFollowerDTO;
    }
}
