package com.revature.revplay.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * This JPA entity represents a personal or communal collection of musical
 * tracks (Playlist).
 * Playlists allow users to curate their own listening experiences by grouping
 * songs
 * that share a specific mood, genre, or activity. Unlike Albums, which are
 * artist-driven,
 * Playlists are user-driven and can be marked as 'Public' for the community to
 * discover
 * or 'Private' for personal use only. They serve as the primary tool for
 * user-generated content organization across the platform.
 * It establishes a custom sequence of tracks that can be shared and enjoyed by
 * others.
 */
@Entity
@Table(name = "playlists")
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
// ####################################### Person4 CODE START #########################################
public class Playlist {

}

// ######################################## Person4 CODE END ##########################################
