package com.admin.school.services.impl;

import com.admin.school.services.FileService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {

    private static final String UPLOAD_DIR = "uploads/";
    private static final String BASE_URL = "http://localhost:8083";

    @Override
    public String saveFile(MultipartFile file, String directory) {
        try {
            // Create directory if it doesn't exist
            Path uploadPath = Paths.get(UPLOAD_DIR + directory);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            } else {
                // Default extension based on content type
                String contentType = file.getContentType();
                if (contentType != null) {
                    if (contentType.startsWith("video/")) {
                        fileExtension = ".mp4";
                    } else if (contentType.startsWith("audio/")) {
                        fileExtension = ".mp3";
                    } else if (contentType.startsWith("image/")) {
                        fileExtension = ".jpg";
                    }
                }
            }
            String filename = UUID.randomUUID().toString() + fileExtension;

            // Save file
            Path filePath = uploadPath.resolve(filename);
            Files.copy(file.getInputStream(), filePath);

            // Return the absolute file URL
            return BASE_URL + "/uploads/" + directory + "/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Failed to save file: " + e.getMessage(), e);
        }
    }

    @Override
    public void deleteFile(String fileUrl) {
        try {
            if (fileUrl != null) {
                Path filePath;
                if (fileUrl.startsWith("/uploads/")) {
                    filePath = Paths.get("." + fileUrl);
                } else if (fileUrl.startsWith("./uploads/")) {
                    filePath = Paths.get(fileUrl);
                } else {
                    // If it's a full URL, extract the path
                    String path = fileUrl.replace(BASE_URL, "");
                    filePath = Paths.get("." + path);
                }
                Files.deleteIfExists(filePath);
                System.out.println("Deleted file: " + filePath.toString());
            }
        } catch (IOException e) {
            System.err.println("Failed to delete file: " + e.getMessage());
            // Don't throw exception for file deletion failures
        }
    }

    @Override
    public String combineImageAndAudio(String imagePath, String audioPath, String outputDirectory) {
        try {
            System.out.println("Starting image and audio combination...");
            System.out.println("Image path: " + imagePath);
            System.out.println("Audio path: " + audioPath);
            System.out.println("Output directory: " + outputDirectory);
            
            // Check if FFmpeg is available
            try {
                ProcessBuilder checkProcess = new ProcessBuilder("ffmpeg", "-version");
                Process check = checkProcess.start();
                int checkExitCode = check.waitFor();
                if (checkExitCode != 0) {
                    throw new RuntimeException("FFmpeg is not available on the system");
                }
                System.out.println("FFmpeg is available");
            } catch (Exception e) {
                System.err.println("FFmpeg check failed: " + e.getMessage());
                throw new RuntimeException("FFmpeg is not installed or not accessible: " + e.getMessage());
            }
            
            // Create output directory if it doesn't exist
            Path uploadPath = Paths.get(UPLOAD_DIR + outputDirectory);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Generate unique filename for the combined video
            String filename = UUID.randomUUID().toString() + ".mp4";
            Path outputPath = uploadPath.resolve(filename);
            System.out.println("Output path: " + outputPath.toString());

            // Build FFmpeg command to combine image and audio
            // -loop 1: loop the image indefinitely
            // -i audioPath: input audio file
            // -c:v libx264: video codec
            // -c:a aac: audio codec
            // -shortest: end when audio ends
            // -pix_fmt yuv420p: pixel format for compatibility
            ProcessBuilder processBuilder = new ProcessBuilder(
                "ffmpeg", "-y", // -y to overwrite output file
                "-loop", "1",
                "-i", imagePath,
                "-i", audioPath,
                "-c:v", "libx264",
                "-c:a", "aac",
                "-shortest",
                "-pix_fmt", "yuv420p",
                outputPath.toString()
            );

            // Redirect error stream to output stream for debugging
            processBuilder.redirectErrorStream(true);

            // Start the process
            Process process = processBuilder.start();
            System.out.println("FFmpeg process started...");

            // Wait for the process to complete
            int exitCode = process.waitFor();
            System.out.println("FFmpeg process completed with exit code: " + exitCode);

            if (exitCode == 0) {
                // Return the URL for the combined video
                String resultUrl = BASE_URL + "/uploads/" + outputDirectory + "/" + filename;
                System.out.println("Combination successful. Result URL: " + resultUrl);
                return resultUrl;
            } else {
                // Read error output for debugging
                String errorOutput = new String(process.getInputStream().readAllBytes());
                System.err.println("FFmpeg error output: " + errorOutput);
                throw new RuntimeException("FFmpeg failed with exit code " + exitCode + ": " + errorOutput);
            }

        } catch (IOException | InterruptedException e) {
            System.err.println("Exception during combination: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Failed to combine image and audio: " + e.getMessage(), e);
        }
    }
} 