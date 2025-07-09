package com.admin.school.services;

import org.springframework.web.multipart.MultipartFile;

public interface FileService {
    String saveFile(MultipartFile file, String directory);
    void deleteFile(String fileUrl);
    String combineImageAndAudio(String imagePath, String audioPath, String outputDirectory);
} 