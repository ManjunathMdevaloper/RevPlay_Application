package com.revature.revplay.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

/**
 * This JPA entity extends the base User record for members with the 'ARTIST'
 * role.
 * It houses specialized professional metadata that defines a creator's public
 * persona.
 * This includes high-resolution banner images, a professional stage name,
 * detailed creative biographies, and direct links to their external social
 * media presence.
 * By using a One-to-One mapping with Shared ID, it ensures that every artist
 * profile is strictly tethered to a valid user account while keeping the
 * base User table lightweight for standard listeners.
 */
@Entity
@Table(name = "artist_profiles")
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
     *
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