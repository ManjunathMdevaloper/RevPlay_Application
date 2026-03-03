package com.revature.revplay.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * This JPA entity represents a single musical track within the RevPlay
 * ecosystem.
 * It is the primary unit of storage for audio content, housing the actual
 * MP3/WAV
 * bytes (BLOB) alongside critical metadata like title, genre, and duration.
 * Each song is strictly linked to an 'Artist' (User) and can optionally be part
 * of a larger 'Album' project. It also tracks its own popularity through a
 * cumulative 'playCount' metric, which influences platform-wide hit charts.
 * This entity acts as the medium through which creators share their voice with
 * the world.
 */
@Entity
@Table(name = "songs")
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Song {

    /**
     * The unique database primary key for the track, managed via an Oracle-style
     * sequence.
     * This ID is used for all media streaming requests and relationship linking.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "song_seq")
    @SequenceGenerator(name = "song_seq", sequenceName = "SONGS_SEQ", allocationSize = 1)
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * The descriptive title of the musical piece as it will appear in searches and
     * players.
     */
    @Column(nullable = false)
    private String title;

    /**
     * The verified creator account that owns and uploaded this specific track.
     * This mandatory link ensures that artist dashboards correctly reflect their
     * catalog.
     */
    @ManyToOne
    @JoinColumn(name = "artist_id", nullable = false)
    @ToString.Exclude
    private User artist;

    /**
     * Optional structural link to an Album project.
     * If a song is part of an album, it inherits additional branding and collection
     * context.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id", insertable = false, updatable = false)
    @ToString.Exclude
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Album album;

    @Column(name = "album_id")
    private Long albumId;

    /**
     * The playback length of the track in seconds, used for UI progress bars and
     * duration labels.
     */
    @Column(nullable = false)
    private Integer duration; // in seconds

    /**
     * The musical category or style assigned to the song for search and category
     * browsing.
     */
    @Column(nullable = false)
    private String genre;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    /**
     * Binary storage for the high-fidelity audio data (BLOB).
     * By storing the music directly in the database, we guarantee that the catalog
     * is fully self-contained and easily backed up alongside its metadata.
     */
    @Lob
    @Column(name = "audio_data", columnDefinition = "BLOB")
    private byte[] audioData;

    /**
     * Binary storage for the song's specific cover artwork (thumbnail).
     * This allows tracks to have unique visual branding separate from their parent
     * album.
     */
    @Lob
    @Column(name = "cover_art_data", columnDefinition = "BLOB")
    private byte[] coverArtData;

    @Column(name = "audio_content_type")
    private String audioContentType;

    @Column(name = "cover_art_content_type")
    private String coverArtContentType;

    /**
     * Generates a virtual web URL that the browser player can use to stream the
     * audio bytes.
     * Maps to the MediaController for secure and efficient byte-range streaming.
     */
    public String getAudioUrl() {
        return (this.audioData != null) ? "/api/media/song/" + this.id + "/audio" : null;
    }

    /**
     * Generates a virtual web URL for the track's visual cover art thumbnail.
     * This is used by the front-end to render the "Now Playing" and search result
     * cards.
     */
    public String getCoverArtUrl() {
        return (this.coverArtData != null) ? "/api/media/song/" + this.id + "/cover" : null;
    }

    /**
     * A persistent counter of every successful listener engagement with the track.
     * This data point is crucial for the platform's trending algorithms and
     * popularity charts.
     */
    @Column(name = "play_count")
    private Long playCount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Lifecycle hook that ensures every new track starts with a valid creation date
     * and a clean zero play count before it reaches the database.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (playCount == null)
            playCount = 0L;
    }
}
