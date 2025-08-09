package com.admin.school.services.impl;

import com.admin.school.controllers.utils.JwtUtil;
import com.admin.school.dto.JwtPayloadDTO;
import com.admin.school.dto.ValidateTokenDTO;
import com.admin.school.dto.user.UserRequestGoogleDTO;
import com.admin.school.exception.InvalidTokenException;
import com.admin.school.exception.UserNotFoundException;
import com.admin.school.models.*;
import com.admin.school.repository.OrgSessionRepository;
import com.admin.school.repository.OrganizationRepository;
import com.admin.school.repository.SessionRepository;
import com.admin.school.repository.UserRepository;
import com.admin.school.services.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final OrganizationRepository organizationRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final SecretKey secretKey;
    private final SessionRepository sessionRepository;
    private final OrgSessionRepository orgSessionRepository;

    public AuthServiceImpl(UserRepository userRepository, OrganizationRepository organizationRepository, BCryptPasswordEncoder bCryptPasswordEncoder, SecretKey secretKey, SessionRepository sessionRepo, OrgSessionRepository orgSessionRepository) {
        this.userRepository = userRepository;
        this.organizationRepository = organizationRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.secretKey = secretKey;
        this.sessionRepository = sessionRepo;
        this.orgSessionRepository = orgSessionRepository;
    }

    @Override
    public Session login(String email, String password) {
        log.info("Login attempt");
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            throw new UserNotFoundException("User not found with email: " + email);
        }

//        if (!bCryptPasswordEncoder.matches(password, user.get().getPassword())) {
//            throw new UserNotFoundException("Invalid Credential");
//        }
        if(!password.equals(user.get().getPassword())){
            throw new UserNotFoundException("Invalid Credentials!!!");
        }

        Map<String, Object> jsonForJWT = new HashMap<>();
        jsonForJWT.put("authorId", user.get().getId());
        jsonForJWT.put("createdAt", new Date());
        jsonForJWT.put("expiresAt", new Date(LocalDate.now().plusDays(3).toEpochDay()));

        JwtUtil.setSecretKey(secretKey);
        String token = JwtUtil.generateToken(jsonForJWT);
        Session session = new Session();
        session.setToken(token);
        session.setSessionStatus(SessionStatus.ACTIVE);
        session.setUser(user.get());
        session.setExpiryAt(Date.from(LocalDate.now().plusDays(3).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        session = sessionRepository.save(session);

        return session;
    }

    @Override
    public User signup(String email, String password, String name) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isPresent()) {
            throw new UserNotFoundException("User already exists with email: " + email);
        }
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPassword(password);
        newUser.setUsername(name);
        newUser = userRepository.save(newUser);
        return newUser;
    }

    @Override
    public void validateUser(String token, String userId) {
        try {
            String[] chunks = token.split("\\.");
            Base64.Decoder decoder = Base64.getUrlDecoder();
            String header = new String(decoder.decode(chunks[0]));
            String payload = new String(decoder.decode(chunks[1]));
            ObjectMapper objectMapper = new ObjectMapper();
            SessionStatus response;
            JwtPayloadDTO jwtPayload = null;
            jwtPayload = objectMapper.readValue(payload, JwtPayloadDTO.class);
            ValidateTokenDTO validateTokenDTO = new ValidateTokenDTO(jwtPayload.getAuthorId(), token);
            validateToken(validateTokenDTO);
            if (!jwtPayload.getAuthorId().equals(userId)){
                throw new InvalidTokenException("Invalid Token");
            }
        } catch (InvalidTokenException e) {
            throw new InvalidTokenException("Invalid Token");
        } catch (Exception e) {
            log.error("Error validating user token: {}", e.getMessage());
            throw new InvalidTokenException("Invalid Token");
        }
    }

    public void validateOrg(String token, String orgId){
        try {
            String[] chunks = token.split("\\.");
            Base64.Decoder decoder = Base64.getUrlDecoder();
            String header = new String(decoder.decode(chunks[0]));
            String payload = new String(decoder.decode(chunks[1]));
            ObjectMapper objectMapper = new ObjectMapper();
            SessionStatus response;
            JwtPayloadDTO jwtPayload = null;
            jwtPayload = objectMapper.readValue(payload, JwtPayloadDTO.class);
            ValidateTokenDTO validateTokenDTO = new ValidateTokenDTO(jwtPayload.getAuthorId(), token);
            validateToken(validateTokenDTO);
            if (!jwtPayload.getAuthorId().equals(orgId)){
                throw new InvalidTokenException("Invalid Token");
            }
        } catch (InvalidTokenException e) {
            throw new InvalidTokenException("Invalid Token");
        } catch (Exception e) {
            log.error("Error validating org token: {}", e.getMessage());
            throw new InvalidTokenException("Invalid Token");
        }
    }

    @Override
    public OrgSession loginOrganization(String email, String password) {
        log.info("Login attempt for Organization");
        Optional<Organization> organization = organizationRepository.findByEmail(email);
        if (organization.isEmpty()) {
            throw new UserNotFoundException("Organization not found with email: " + email);
        }

//        if (!bCryptPasswordEncoder.matches(password, user.get().getPassword())) {
//            throw new UserNotFoundException("Invalid Credential");
//        }
        if(!password.equals(organization.get().getPassword())){
            throw new UserNotFoundException("Invalid Credentials!!!");
        }

        Map<String, Object> jsonForJWT = new HashMap<>();
        jsonForJWT.put("authorId", organization.get().getId());
        jsonForJWT.put("createdAt", new Date());
        jsonForJWT.put("expiresAt", new Date(LocalDate.now().plusDays(3).toEpochDay()));

        JwtUtil.setSecretKey(secretKey);
        String token = JwtUtil.generateToken(jsonForJWT);
        OrgSession session = new OrgSession();
        session.setToken(token);
        session.setSessionStatus(SessionStatus.ACTIVE);
        session.setOrganization(organization.get());
        session.setExpiryAt(Date.from(LocalDate.now().plusDays(3).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        session = orgSessionRepository.save(session);

        return session;
    }

    @Override
    public Organization signupOrganization(String email, String password) {
        Optional<Organization> organization = organizationRepository.findByEmail(email);
        if (organization.isPresent()) {
            throw new UserNotFoundException("Organization already exists with email: " + email);
        }
        Organization newOrganization = new Organization();
        newOrganization.setEmail(email);
        newOrganization.setPassword(password);
        newOrganization.setName(""); // Set empty name so routing redirects to profile completion
        newOrganization = organizationRepository.save(newOrganization);
        return newOrganization;
    }

    @Override
    public Session loginGoogle(UserRequestGoogleDTO userRequestDTO) {
        log.info("Login attempt for Google");
        Optional<User> user = userRepository.findByEmail(userRequestDTO.getEmail());
        Session session = new Session();
        if (user.isEmpty()) {
            User newUser = new User();
            newUser.setEmail(userRequestDTO.getEmail());
            newUser.setUsername(userRequestDTO.getName());
            newUser.setProfilePictureUrl(userRequestDTO.getPicture());
            newUser.setPassword(userRequestDTO.getEmail());
            newUser.setCreatedAt(new Date());

            newUser = userRepository.save(newUser);
            user = Optional.of(newUser);

            if( newUser.getProfileStatus() == null || newUser.getProfileStatus().isEmpty()) {
                newUser.setProfileStatus("CREATED");
                userRepository.save(newUser);
            }

            session.setRegistered(newUser.getProfileStatus() != null && (newUser.getProfileStatus().equals("COMPLETED") || newUser.getProfileStatus().equals("VERIFIED")));
            session.setUser(newUser);
//            throw new UserNotFoundException("User not found with email: " + userRequestDTO.getEmail());
        }
        else{
            session.setRegistered(user.get().getProfileStatus().equals("COMPLETED") || user.get().getProfileStatus().equals("VERIFIED"));
            session.setUser(user.get());
        }
        Map<String, Object> jsonForJWT = new HashMap<>();
        jsonForJWT.put("authorId", user.get().getId());
        jsonForJWT.put("createdAt", new Date());
        jsonForJWT.put("expiresAt", new Date(LocalDate.now().plusDays(3).toEpochDay()));

        JwtUtil.setSecretKey(secretKey);
        String token = JwtUtil.generateToken(jsonForJWT);

        session.setToken(token);
        session.setSessionStatus(SessionStatus.ACTIVE);
        session.setExpiryAt(Date.from(LocalDate.now().plusDays(3).atStartOfDay(ZoneId.systemDefault()).toInstant()));
        session = sessionRepository.save(session);

        return session;
    }


    public void validateToken(ValidateTokenDTO token) {
        Optional<Session> sessionOptional = sessionRepository.findByToken(token.getToken());
        if (sessionOptional.isPresent()) {
            Optional<User> user = userRepository.findById(UUID.fromString(token.getUserId()));
            if (!(sessionOptional.get().getSessionStatus().equals(SessionStatus.ACTIVE))) {
                throw new InvalidTokenException("Invalid Token");
            } else if (sessionOptional.get().getExpiryAt().before(new Date())) {
                sessionOptional.get().setSessionStatus(SessionStatus.ENDED);
                sessionRepository.save(sessionOptional.get());
                throw new InvalidTokenException("Invalid Token");
            }
            if (user.isEmpty()) {
                throw new UserNotFoundException("Invalid User");
            }
        }
        else{
            Optional<OrgSession> orgSessionOptional = orgSessionRepository.findByToken(token.getToken());
            if (orgSessionOptional.isPresent()) {
                Optional<Organization> organization = organizationRepository.findById(UUID.fromString(token.getUserId()));
                if (!(orgSessionOptional.get().getSessionStatus().equals(SessionStatus.ACTIVE))) {
                    throw new InvalidTokenException("Invalid Token");
                } else if (orgSessionOptional.get().getExpiryAt().before(new Date())) {
                    orgSessionOptional.get().setSessionStatus(SessionStatus.ENDED);
                    orgSessionRepository.save(orgSessionOptional.get());
                    throw new InvalidTokenException("Invalid Token");
                }
                if (organization.isEmpty()) {
                    throw new UserNotFoundException("Invalid User");
                }
            }
        }
    }

    @Override
    public void logout(String token) {
        log.info("Logout attempt for token: {}", token);
        
        // Try to find and invalidate user session
        Optional<Session> sessionOptional = sessionRepository.findByToken(token);
        if (sessionOptional.isPresent()) {
            Session session = sessionOptional.get();
            session.setSessionStatus(SessionStatus.ENDED);
            sessionRepository.save(session);
            log.info("User session invalidated successfully");
            return;
        }
        
        // Try to find and invalidate organization session
        Optional<OrgSession> orgSessionOptional = orgSessionRepository.findByToken(token);
        if (orgSessionOptional.isPresent()) {
            OrgSession session = orgSessionOptional.get();
            session.setSessionStatus(SessionStatus.ENDED);
            orgSessionRepository.save(session);
            log.info("Organization session invalidated successfully");
            return;
        }
        
        log.warn("No active session found for token: {}", token);
    }

    @Override
    public String getUserIdFromToken(String token) {
        try {
            String[] chunks = token.split("\\.");
            Base64.Decoder decoder = Base64.getUrlDecoder();
            String payload = new String(decoder.decode(chunks[1]));
            ObjectMapper objectMapper = new ObjectMapper();
            JwtPayloadDTO jwtPayload = objectMapper.readValue(payload, JwtPayloadDTO.class);
            return jwtPayload.getAuthorId();
        } catch (Exception e) {
            log.error("Error extracting user ID from token: {}", e.getMessage());
            throw new InvalidTokenException("Invalid Token");
        }
    }

}
