package com.revature.revplay.dto;

import com.revature.revplay.entity.Album;
import com.revature.revplay.entity.Playlist;
import com.revature.revplay.entity.Song;
import com.revature.revplay.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * This Data Transfer Object (DTO) serves as a unified container for many types
 * of search results.
 * When a user performs a search, the system may find matching songs, artists,
 * albums, or playlists.
 * Instead of returning these separately, this object bundles all related
 * findings into a single delivery.
 * This allows the front-end to render a categorized and comprehensive view of
 * all system matches.
 * It is the core data structure used by the SearchService to relay findings
 * back to the UI.
 * This object is fundamental to the platform's ability to provide a
 * "Google-like" discovery experience.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchResultDto {
    /**
     * Lists containing individual entities that matched the user's search query
     * across different categories.
     */
    private List<Song> songs;
    private List<User> artists;
    private List<Album> albums;
    private List<Playlist> playlists;

    /**
     * A utility method to quickly determine if the search returned zero results
     * across all categories.
     * This method is essential for:
     * 1. Displaying a "No Results Found" message to the user when appropriate.
     * 2. determining if specialized UI sections (like artist results) should be
     * hidden.
     * 3. helping the controller decide whether to show browsing categories instead
     * of results.
     * 4. Simplifying result checking logic in the view layer (Thymeleaf).
     */
    public boolean isEmpty() {
        return (songs == null || songs.isEmpty()) &&
                (artists == null || artists.isEmpty()) &&
                (albums == null || albums.isEmpty()) &&
                (playlists == null || playlists.isEmpty());
    }
}