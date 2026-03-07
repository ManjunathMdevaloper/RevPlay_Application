package com.revature.revplay.controller;

import com.revature.revplay.service.SongService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * This specialized REST controller handles the real-time analytics for music
 * playback.
 * It provides a lightweight API endpoint that the front-end music player calls
 * whenever
 * a track begins playing or reaches a significant listening milestone.
 * By mapping to "/api/stream", it separates the background analytics logic from
 * the primary page-rendering controllers. It ensures that every play is
 * accurately
 * captured to drive the platform's trending algorithms and artist payment
 * metrics.
 */



@RestController
@RequestMapping("/api/stream")
public class StreamController {
    private final SongService songService;

    /**
     * Standard constructor for injecting the SongService dependency.
     *
     * This dependency allows the controller to:
     * 1. Register play events in the master tracking database.
     * 2. update the aggregate popularity scores for both tracks and artists.
     * 3. Integrate with the user's personal listening history for profile
     * transparency.
     * 4. Maintain high-concurrency performance by using a dedicated service layer
     * for updates.
     * 5. This setup ensures that skip-counting and discovery logic is always based
     * on fresh data.
     */
    public StreamController(SongService songService) {
        this.songService = songService;
    }

    /**
     * Increments the play count and history for a specific song ID.
     *
     * The stream recording logic handles:
     * 1. Identifying the song being played through a path variable.
     * 2. Capturing the current user's session from the authentication context (if
     * available).
     * 3. triggering a permanent record in the 'History' table for logged-in
     * listeners.
     * 4. Atomicly incrementing the global play count to prevent "lost counts"
     * during high traffic.
     * 5. returning a clean 200 OK response to signal the front-end that data was
     * stored.
     * 6. This is the primary engine behind the "Top Trending" and "Recently Played"
     * features.
     */
    @PostMapping("/{songId}/increment")
    public ResponseEntity<String> incrementStream(@PathVariable("songId") Long songId,
                                                  org.springframework.security.core.Authentication auth) {
        String username = (auth != null) ? auth.getName() : null;
        songService.recordPlay(songId, username);

        return ResponseEntity.ok("Stream recorded cleanly.");
    }

}


