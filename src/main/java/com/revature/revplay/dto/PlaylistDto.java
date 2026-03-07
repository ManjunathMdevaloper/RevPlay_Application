package com.revature.revplay.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * This Data Transfer Object (DTO) carries the branding and configuration
 * details for user playlists.
 * It is used for creating new collections and updating the metadata of existing
 * ones.
 * By using this object, we can safely transmit the user's intent—such as the
 * playlist name
 * and its public visibility—without exposing the underlying database
 * relationships.
 * It acts as the primary data model for the "Create Playlist" and "Edit
 * Playlist" forms.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class PlaylistDto {
    /**
     * Unique identifier for the collection, used mainly during update and delete
     * actions.
     */
    private Long id;

    /**
     * The public-facing name of the playlist, chosen by the creator.
     */
    private String name;

    /**
     * A creative brief or summary of the music theme within this specific
     * collection.
     */
    private String description;

    /**
     * A flag determining if other users on the platform can discover and listen to
     * this list.
     * If true, it will appear in global discovery; if false, it remains strictly
     * private.
     */
    private boolean isPublic;
}
