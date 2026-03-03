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

// ####################################### Person4 CODE START #########################################
public class PlaylistDto {

}

// ######################################## Person4 CODE END ##########################################
