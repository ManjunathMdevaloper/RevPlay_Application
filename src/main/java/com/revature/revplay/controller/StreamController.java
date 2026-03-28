package com.revature.revplay.controller;

import com.revature.revplay.service.SongService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/stream")
@Log4j2
public class StreamController {

    private final SongService songService;

    
    public StreamController(SongService songService) {
        this.songService = songService;
    }

    
    @PostMapping("/{songId}/increment")
    public ResponseEntity<String> incrementStream(@PathVariable("songId") Long songId,
            org.springframework.security.core.Authentication auth) {
        String username = (auth != null) ? auth.getName() : null;
        log.debug("Incrementing stream for song ID: {} (User: {})", songId, username != null ? username : "Anonymous");
        songService.recordPlay(songId, username);

        return ResponseEntity.ok("Stream recorded cleanly.");
    }
}
