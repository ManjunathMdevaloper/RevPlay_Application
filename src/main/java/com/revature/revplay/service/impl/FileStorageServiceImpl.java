package com.revature.revplay.service.impl;

import com.revature.revplay.service.FileStorageService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
@Log4j2
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
                log.info("Creating directory for uploads: {}", uploadPath);
                Files.createDirectories(uploadPath);
            }
            String originalName = file.getOriginalFilename();
            log.debug("Original filename: {}", originalName);
            if (originalName == null || originalName.trim().isEmpty()) {
                originalName = "upload";
            }
            String fileName = UUID.randomUUID().toString() + "_" + originalName.replaceAll("[^a-zA-Z0-9.\\-]", "_");
            Path filePath = uploadPath.resolve(fileName);
            log.info("Storing file to: {}", filePath);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return fileName;
        } catch (IOException e) {
            log.error("Failed to store file in directory: {}", subDir, e);
            throw new RuntimeException("Could not store file", e);
        }
    }

    @Override
    public void deleteFile(String fileName, String subDir) {
        if (fileName == null || fileName.isEmpty()) {
            return;
        }

        try {
            
            String strippedName = fileName;
            if (strippedName.contains("/")) {
                strippedName = strippedName.substring(strippedName.lastIndexOf("/") + 1);
            }

            Path filePath = Paths.get(UPLOAD_DIR + subDir).resolve(strippedName);
            log.info("Attempting to delete file: {}", filePath);
            boolean deleted = Files.deleteIfExists(filePath);
            if (deleted) {
                log.debug("Successfully deleted file: {}", filePath);
            } else {
                log.warn("File to delete not found: {}", filePath);
            }
        } catch (IOException e) {
            log.error("Failed to delete file: {} in subdirectory: {}", fileName, subDir, e);
            throw new RuntimeException("Could not delete file", e);
        }
    }
}
