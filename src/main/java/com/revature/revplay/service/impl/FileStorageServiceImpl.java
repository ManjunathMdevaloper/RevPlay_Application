package com.revature.revplay.service.impl;

import com.revature.revplay.service.FileStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageServiceImpl implements FileStorageService {

    private final String UPLOAD_DIR = "uploads/";

    @Override
    public String storeFile(MultipartFile file, String subDir) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        try {
            Path uploadPath = Paths.get(UPLOAD_DIR + subDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String originalName = file.getOriginalFilename();
            if (originalName == null || originalName.trim().isEmpty()) {
                originalName = "upload";
            }
            String fileName = UUID.randomUUID().toString() + "_" + originalName.replaceAll("[^a-zA-Z0-9.\\-]", "_");
            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException e) {
            throw new RuntimeException("Could not store file", e);
        }
    }

    @Override
    public void deleteFile(String fileName, String subDir) {
        if (fileName == null || fileName.isEmpty()) {
            return;
        }

        try {
            // Strip the preceding paths if someone saved the entire route
            String strippedName = fileName;
            if (strippedName.contains("/")) {
                strippedName = strippedName.substring(strippedName.lastIndexOf("/") + 1);
            }

            Path filePath = Paths.get(UPLOAD_DIR + subDir).resolve(strippedName);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Could not delete file", e);
        }
    }
}
