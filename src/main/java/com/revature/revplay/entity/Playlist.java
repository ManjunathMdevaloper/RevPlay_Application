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
public class Playlist {

    /**
     * The unique database primary key for the playlist, managed via a dedicated
     * sequence.
     * This ID is used for routing to the playlist detail page and managing song
     * additions.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "playlist_seq")
    @SequenceGenerator(name = "playlist_seq", sequenceName = "PLAYLISTS_SEQ", allocationSize = 1)
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * The descriptive name of the collection as it will appear in the user's
     * library.
     */
    @Column(nullable = false)
    private String name;

    /**
     * An optional field for a thematic description or "vibe" summary of the
     * playlist's content.
     */
    @Column(length = 2000)
    private String description;

    /**
     * Flag determining the visibility of the collection to other members of the
     * RevPlay community.
     * Public playlists are indexed in the global search engine for discovery by
     * peers.
     */
    @Column(name = "is_public", nullable = false)
    private boolean isPublic;

    /**
     * The owner account that created and maintains this specific collection.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    /**
     * The dynamic collection of tracks currently assigned to this playlist.
     * This many-to-many relationship allows for flexible curation and re-ordering.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "playlist_songs", joinColumns = @JoinColumn(name = "playlist_id"), inverseJoinColumns = @JoinColumn(name = "song_id"))
    @ToString.Exclude
    @Builder.Default
    private java.util.Set<Song> songs = new java.util.HashSet<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Lifecycle hook that captures the exact timestamp of the playlist's inception.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
