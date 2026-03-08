package com.revature.revplay.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * This JPA entity represents a professional musical project (Album) on the
 * platform.
 * An Album serves as a thematic collection of songs released by an artist
 * together.
 * It houses collective branding, such as a high-resolution cover art (BLOB) and
 * descriptive liner notes, providing a formal structure for musical releases.
 * By grouping related tracks, it allows listeners to experience an artist's
 * vision
 * in a more cohesive and professional format than single-track uploads.
 * It acts as the anchor for multi-track storytelling and professional
 * discography.
 */
@Entity
@Table(name = "albums")
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Album {

    /**
     * The unique database primary key for the album, managed via an Oracle-style
     * sequence.
     * This ID is used for generating web-accessible URLs for the album's cover
     * artwork.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "album_seq")
    @SequenceGenerator(name = "album_seq", sequenceName = "ALBUMS_SEQ", allocationSize = 1)
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * The formal title of the album as displayed in the public catalog and artist
     * profile.
     */
    @Column(nullable = false)
    private String name;

    /**
     * An optional multi-paragraph field for the artist to share the story or
     * context of the project.
     */
    @Column(length = 2000)
    private String description;

    /**
     * The official date the album project was finalized and made available to the
     * public.
     */
    @Column(name = "release_date")
    private LocalDate releaseDate;

    /**
     * Binary storage for the album's primary visual branding (BLOB).
     * This high-resolution artwork represents the entire project across the UI.
     */
    @Lob
    @Column(name = "cover_art_data", columnDefinition = "BLOB")
    private byte[] coverArtData;

    @Column(name = "cover_art_content_type")
    private String coverArtContentType;

    /**
     * Generates a virtual web URL for fetching the album's branding artwork.
     * Maps to the MediaController for delivery to the browser.
     */
    public String getCoverArtUrl() {
        return (this.coverArtData != null) ? "/api/media/album/" + this.id + "/cover" : null;
    }

    /**
     * The collection of all songs that comprise this specific album project.
     * This one-to-many relationship defines the project's musical content.
     */
    @OneToMany(mappedBy = "album", fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    @com.fasterxml.jackson.annotation.JsonIgnore
    private java.util.List<Song> songs = new java.util.ArrayList<>();

    /**
     * Calculates the total runtime of the entire album by summing its member
     * tracks.
     * Used for displaying project length on the album detail and artist pages.
     */
    public int getTotalDuration() {
        return songs.stream().mapToInt(Song::getDuration).sum();
    }

    /**
     * Determines the primary musical style of the album based on its track
     * composition.
     */
    public String getGenre() {
        if (songs.isEmpty())
            return "Unknown";
        return songs.get(0).getGenre(); // Assuming one genre per album
    }

    /**
     * The creator account that owns and released this professional project.
     */
    @ManyToOne
    @JoinColumn(name = "artist_id", nullable = false)
    @ToString.Exclude
    @com.fasterxml.jackson.annotation.JsonIgnore
    private User artist;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Lifecycle hook to capture the precise moment the album record was created in
     * the system.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}