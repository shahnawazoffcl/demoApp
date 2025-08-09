package com.admin.school.controllers.utils;

import com.admin.school.dto.SessionResponseDTO;
import com.admin.school.dto.user.UserResponseDTO;
import com.admin.school.models.Session;
import com.admin.school.models.User;

import java.util.Date;

public class AuthControllerUtils {

    public static UserResponseDTO mapUserToUserResponse(User user) {
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        userResponseDTO.setEmail(user.getEmail());
        userResponseDTO.setUsername(user.getUsername());
        userResponseDTO.setProfilePictureUrl(user.getProfilePictureUrl());
        userResponseDTO.setRole(user.getRole());
        userResponseDTO.setCreatedAt(user.getCreatedAt()==null? new Date() :user.getCreatedAt());
        userResponseDTO.setConnectionsCount(user.getConnections()== null ? 0 : user.getConnections().size());
        userResponseDTO.setId(user.getId());
        userResponseDTO.setProfileStatus(user.getProfileStatus());

        return userResponseDTO;
    }

    public static SessionResponseDTO getSessionResponseDTO(User user, Session session) {
        SessionResponseDTO sessionResponseDTO = new SessionResponseDTO();
        sessionResponseDTO.setToken(session.getToken());
        sessionResponseDTO.setAuthor(mapUserToUserResponse(user));
        sessionResponseDTO.setExpiryAt(session.getExpiryAt());
        sessionResponseDTO.setRegistered(session.isRegistered());
        return sessionResponseDTO;
    }
}
