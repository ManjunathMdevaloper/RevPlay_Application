package com.revature.revplay.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "song_seq")
    @SequenceGenerator(name = "song_seq", sequenceName = "SONGS_SEQ", allocationSize = 1)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String title;

    @ManyToOne
    @JoinColumn(name = "artist_id", nullable = false)
    @ToString.Exclude
    private User artist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "album_id", insertable = false, updatable = false)
    @ToString.Exclude
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Album album;

    @Column(name = "album_id")
    private Long albumId;

    @Column(nullable = false)
    private Integer duration; 

    @Column(nullable = false)
    private String genre;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Lob
    @Column(name = "audio_data", columnDefinition = "BLOB")
    private byte[] audioData;

    @Lob
    @Column(name = "cover_art_data", columnDefinition = "BLOB")
    private byte[] coverArtData;

    @Column(name = "audio_content_type")
    private String audioContentType;

    @Column(name = "cover_art_content_type")
    private String coverArtContentType;

    public String getAudioUrl() {
        return (this.audioData != null) ? "/api/media/song/" + this.id + "/audio" : null;
    }

    public String getCoverArtUrl() {
        return (this.coverArtData != null) ? "/api/media/song/" + this.id + "/cover" : null;
    }

    @Column(name = "play_count")
    private Long playCount;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (playCount == null)
            playCount = 0L;
    }
}