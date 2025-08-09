package com.admin.school.controllers.utils;

import com.admin.school.dto.user.UserFollowerDTO;
import com.admin.school.dto.user.UserResponseDTO;
import com.admin.school.models.User;

import java.util.Date;

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

    public static UserResponseDTO mapUserToUserResponse(User updatedUser) {
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setId(updatedUser.getId());
        userResponseDTO.setEmail(updatedUser.getEmail());
        userResponseDTO.setUsername(updatedUser.getUsername());
        userResponseDTO.setProfilePictureUrl(updatedUser.getProfilePictureUrl());
        userResponseDTO.setRole(updatedUser.getRole());
        userResponseDTO.setCreatedAt(updatedUser.getCreatedAt() == null ? new Date() : updatedUser.getCreatedAt());
        userResponseDTO.setConnectionsCount(updatedUser.getConnections() == null ? 0 : updatedUser.getConnections().size());
        userResponseDTO.setProfileStatus(updatedUser.getProfileStatus());

        return userResponseDTO;
    }
}
