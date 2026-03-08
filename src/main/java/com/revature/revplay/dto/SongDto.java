package com.revature.revplay.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * This Data Transfer Object (DTO) represents the essential metadata for a
 * single music track.
 * It is used primarily during the song upload and editing phases of the
 * artist's workflow.
 * By using this object, we can bundle title, genre, and duration into a single
 * package for easy transmission.
 * It acts as the blueprint for creating or updating a permanent 'Song' entity
 * in the database.
 * This decoupled approach allows us to validate track information before
 * processing heavy audio data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SongDto {
    /**
     * Unique track identification and descriptive naming fields.
     * These identify the song within the global library and define its public
     * title.
     */
    private Long id;
    private String title;

    /**
     * Categorization fields that help the discovery and search engines.
     * This links the track to its parental project (Album) and its musical style
     * (Genre).
     * The duration field ensures the user knows the length of the listening
     * experience.
     */
    private Long albumId;
    private String genre;
    private Integer duration;
}