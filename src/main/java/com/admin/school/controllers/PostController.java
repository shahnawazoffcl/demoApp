package com.admin.school.controllers;


import com.admin.school.controllers.utils.PostControllerUtils;
import com.admin.school.dto.post.PostRequestDTO;
import com.admin.school.dto.post.PostResponseDTO;
import com.admin.school.dto.post.PostsProcessDTO;
import com.admin.school.models.Post;
import com.admin.school.services.AuthService;
import com.admin.school.services.FileService;
import com.admin.school.services.PostsService;
import com.admin.school.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/post")
public class PostController {

    private final PostsService postsService;
    private final AuthService authService;
    private final FileService fileService;
    private final UserService userService;

    public PostController(PostsService postsService, AuthService authService, FileService fileService, UserService userService) {
        this.postsService = postsService;
        this.authService = authService;
        this.fileService = fileService;
        this.userService = userService;
    }


    @PostMapping("/create")
    public ResponseEntity<?> createPost(
            @RequestHeader("token") String token, 
            @RequestParam("postContent") String postContent,
            @RequestParam(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "video", required = false) MultipartFile video,
            @RequestParam(value = "audio", required = false) MultipartFile audio,
            @RequestParam("userId") String userId,
            @RequestParam(value = "organizationId", required = false) String organizationId) {
        
        // Create PostRequestDTO from form data
        PostRequestDTO postRequestDTO = new PostRequestDTO();
        postRequestDTO.setContent(postContent);
        postRequestDTO.setUserId(userId);
        postRequestDTO.setOrganizationId(organizationId);
        
        Post post = PostControllerUtils.mapPostRequestToPostModel(postRequestDTO);
        
        // Handle file uploads if present
        String imagePath = null;
        String audioPath = null;
        
        if (image != null && !image.isEmpty()) {
            String imageUrl = fileService.saveFile(image, "images");
            imagePath = imageUrl.replace("http://localhost:8083", ".");
            post.setMediaUrl(imageUrl);
            System.out.println("Image URL generated: " + imageUrl);
            System.out.println("Image path for combination: " + imagePath);
        }
        if (video != null && !video.isEmpty()) {
            String videoUrl = fileService.saveFile(video, "videos");
            post.setMediaUrl(videoUrl);
            System.out.println("Video URL generated: " + videoUrl);
        }
        if (audio != null && !audio.isEmpty()) {
            String audioUrl = fileService.saveFile(audio, "audio");
            audioPath = audioUrl.replace("http://localhost:8083", ".");
            post.setMediaUrl(audioUrl);
            System.out.println("Audio URL generated: " + audioUrl);
            System.out.println("Audio path for combination: " + audioPath);
        }
        
        // If both image and audio are present, store them separately
        if (imagePath != null && audioPath != null) {
            try {
                System.out.println("Storing image and audio separately...");
                System.out.println("Image path: " + imagePath);
                System.out.println("Audio path: " + audioPath);
                
                // Keep the original image URL
                String originalImageUrl = imagePath.replace("./uploads/", "http://localhost:8083/uploads/");
                post.setMediaUrl(originalImageUrl);
                
                // Store audio URL separately (we'll need to add this field to the Post model)
                String audioUrl = audioPath.replace("./uploads/", "http://localhost:8083/uploads/");
                post.setAudioUrl(audioUrl); // This will be added to the Post model
                
                System.out.println("Image URL: " + originalImageUrl);
                System.out.println("Audio URL: " + audioUrl);
                System.out.println("Final media URL set to: " + post.getMediaUrl());
                
            } catch (Exception e) {
                System.err.println("Failed to process image and audio: " + e.getMessage());
                e.printStackTrace();
                // Keep the original image if processing fails
                String fallbackImageUrl = imagePath.replace("./uploads/", "http://localhost:8083/uploads/");
                post.setMediaUrl(fallbackImageUrl);
                System.out.println("Fallback to image URL: " + fallbackImageUrl);
            }
        } else {
            System.out.println("No combination needed. Final media URL: " + post.getMediaUrl());
        }
        
        if(organizationId == null){
            authService.validateUser(token, userId);
            post = postsService.createPost(post,"USER" , userId);
        }
        else{
            authService.validateUser(token, organizationId);
            post = postsService.createPost(post, "ORGANIZATION" , organizationId);
        }
        PostResponseDTO postResponseDTO = PostControllerUtils.mapPostToPostResponseDTO(post);
        return ResponseEntity.ok(postResponseDTO);
    }

    @GetMapping("{userId}/posts")
    public ResponseEntity<List<PostResponseDTO>> getAllPostsForUser(@RequestHeader("token") String token, @PathVariable("userId") String userId) {
        authService.validateUser(token, userId);
        List<PostsProcessDTO> postList = postsService.getAllPostsForUser(userId);
        List<PostResponseDTO> postResponseDTOList = new ArrayList<>();
        for (PostsProcessDTO post : postList) {
            PostResponseDTO postResponseDTO = PostControllerUtils.mapPostProcessDTOToPostResponseDTO(post);
            postResponseDTOList.add(postResponseDTO);
        }
        return ResponseEntity.ok(postResponseDTOList);
    }

    @GetMapping("/test-video")
    public ResponseEntity<String> testVideo() {
        return ResponseEntity.ok("Video endpoint is working");
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDTO> getPostById(@RequestHeader("token") String token, @PathVariable("postId") String postId) {
        try {
            String userId = authService.getUserIdFromToken(token);
            authService.validateUser(token, userId);
            
            Post post = postsService.getPost(postId);
            PostResponseDTO postResponseDTO = PostControllerUtils.mapPostToPostResponseDTO(post, userId, userService);
            return ResponseEntity.ok(postResponseDTO);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
