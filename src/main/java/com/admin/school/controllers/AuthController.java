package com.admin.school.controllers;


import com.admin.school.controllers.utils.AuthControllerUtils;
import com.admin.school.controllers.utils.GoogleTokenUtils;
import com.admin.school.controllers.utils.OrganizationControllerUtils;
import com.admin.school.dto.SessionResponseDTO;
import com.admin.school.dto.user.UserRequestDTO;
import com.admin.school.dto.user.UserRequestGoogleDTO;
import com.admin.school.models.OrgSession;
import com.admin.school.models.Session;
import com.admin.school.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController()
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private AuthService authService;


    @PostMapping("/login")
    public ResponseEntity<SessionResponseDTO> login(@RequestBody UserRequestDTO userRequestDTO) {
        Session session = authService.login(userRequestDTO.getEmail(), userRequestDTO.getPassword());
        SessionResponseDTO sessionResponseDTO = AuthControllerUtils.getSessionResponseDTO(session.getUser(), session);
        sessionResponseDTO.setEntityType("USER");
        
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Authorization", session.getToken());
        responseHeaders.add("ExpiryAt", session.getExpiryAt().toString());
        responseHeaders.add("Access-Control-Expose-Headers", "Authorization");
        responseHeaders.add("Access-Control-Expose-Headers", "ExpiryAt");

        return ResponseEntity.ok().headers(responseHeaders).body(sessionResponseDTO);
    }

    @PostMapping("/login-google")
    public ResponseEntity<SessionResponseDTO> loginGoogle(@RequestBody String token) {
        UserRequestGoogleDTO userRequestDTO = GoogleTokenUtils.verifyAndGetUserRequestDTO(token);
        Session session = authService.loginGoogle(userRequestDTO);
        SessionResponseDTO sessionResponseDTO = AuthControllerUtils.getSessionResponseDTO(session.getUser(), session);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Authorization", session.getToken());
        responseHeaders.add("ExpiryAt", session.getExpiryAt().toString());
        responseHeaders.add("Access-Control-Expose-Headers", "Authorization");
        responseHeaders.add("Access-Control-Expose-Headers", "ExpiryAt");

        return ResponseEntity.ok().headers(responseHeaders).body(sessionResponseDTO);
    }

    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody UserRequestDTO userRequestDTO) {
        if(userRequestDTO.getRole().equals("USER"))
            authService.signup(userRequestDTO.getEmail(), userRequestDTO.getPassword(), userRequestDTO.getName());
        else if(userRequestDTO.getRole().equals("ORGANIZATION"))
            authService.signupOrganization(userRequestDTO.getEmail(), userRequestDTO.getPassword());
        else
            return ResponseEntity.badRequest().body("Invalid role specified");
        return ResponseEntity.ok("User created successfully");
    }


    @PostMapping("/loginOrganization")
    public ResponseEntity<SessionResponseDTO> loginOrganization(@RequestBody UserRequestDTO userRequestDTO) {
        OrgSession session = authService.loginOrganization(userRequestDTO.getEmail(), userRequestDTO.getPassword());
        
        SessionResponseDTO sessionResponseDTO = new SessionResponseDTO();
        sessionResponseDTO.setToken(session.getToken());
        sessionResponseDTO.setOrganization(OrganizationControllerUtils.mapOrganizationToOrganizationResponseDTO(session.getOrganization()));
        sessionResponseDTO.setEntityType("ORGANIZATION");
        sessionResponseDTO.setExpiryAt(session.getExpiryAt());
        sessionResponseDTO.setRegistered(true); // Organizations are always registered
        
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Authorization", session.getToken());
        responseHeaders.add("ExpiryAt", session.getExpiryAt().toString());
        responseHeaders.add("Access-Control-Expose-Headers", "Authorization");
        responseHeaders.add("Access-Control-Expose-Headers", "ExpiryAt");

        return ResponseEntity.ok().headers(responseHeaders).body(sessionResponseDTO);
    }

    @PostMapping("/signupOrganization")
    public ResponseEntity<String> signupOrganization(@RequestBody UserRequestDTO userRequestDTO) {
        authService.signupOrganization(userRequestDTO.getEmail(), userRequestDTO.getPassword());
        return ResponseEntity.ok("Organization created successfully");
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("token") String token) {
        authService.logout(token);
        return ResponseEntity.ok("Logged out successfully");
    }

}
