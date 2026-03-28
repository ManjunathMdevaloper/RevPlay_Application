package com.revature.revplay.service;

import org.springframework.web.multipart.MultipartFile;

public interface FileStorageService {
    String storeFile(MultipartFile file, String subDir);

    void deleteFile(String fileName, String subDir);
}
