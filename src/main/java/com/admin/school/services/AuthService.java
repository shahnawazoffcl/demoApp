package com.admin.school.services;

import com.admin.school.dto.ValidateTokenDTO;
import com.admin.school.dto.user.UserRequestGoogleDTO;
import com.admin.school.models.OrgSession;
import com.admin.school.models.Organization;
import com.admin.school.models.Session;
import com.admin.school.models.User;

public interface AuthService {
    Session login(String email, String password);
    User signup(String email, String password, String name);
    void validateUser(String token, String userId);

    OrgSession loginOrganization(String email, String password);

    Organization signupOrganization(String email, String password);

    Session loginGoogle(UserRequestGoogleDTO userRequestDTO);
    
    void logout(String token);
    
    String getUserIdFromToken(String token);
}
