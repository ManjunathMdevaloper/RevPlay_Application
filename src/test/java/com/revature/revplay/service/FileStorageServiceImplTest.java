package com.revature.revplay.service;

import com.revature.revplay.service.impl.FileStorageServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

class FileStorageServiceImplTest {

    private final FileStorageServiceImpl fileStorageService = new FileStorageServiceImpl();

    private final String testDir = "test/";

    @AfterEach
    void cleanup() throws Exception {
        Path uploadPath = Paths.get("uploads/" + testDir);
        if (Files.exists(uploadPath)) {
            Files.walk(uploadPath)
                    .sorted((a, b) -> b.compareTo(a))
                    .forEach(path -> {
                        try { Files.deleteIfExists(path); } catch (Exception ignored) {}
                    });
        }
    }

    @Test
    void shouldStoreFileSuccessfully() throws Exception {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "song.mp3",
                "audio/mpeg",
                "test-data".getBytes()
        );

        String fileName = fileStorageService.storeFile(file, testDir);

        assertNotNull(fileName);

        Path storedFile = Paths.get("uploads/" + testDir + fileName);
        assertTrue(Files.exists(storedFile));
    }

    @Test
    void shouldReturnNullWhenFileIsNull() {

        String result = fileStorageService.storeFile(null, testDir);

        assertNull(result);
    }

    @Test
    void shouldDeleteFileSuccessfully() throws Exception {

        MockMultipartFile file = new MockMultipartFile(
                "file",
                "song.mp3",
                "audio/mpeg",
                "test-data".getBytes()
        );

        String fileName = fileStorageService.storeFile(file, testDir);

        fileStorageService.deleteFile(fileName, testDir);

        Path storedFile = Paths.get("uploads/" + testDir + fileName);
        assertFalse(Files.exists(storedFile));
    }

    @Test
    void shouldDoNothingWhenFileNameIsEmpty() {

        assertDoesNotThrow(() ->
                fileStorageService.deleteFile("", testDir)
        );
    }
}