package com.revature.revplay.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "album_seq")
    @SequenceGenerator(name = "album_seq", sequenceName = "ALBUMS_SEQ", allocationSize = 1)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 2000)
    private String description;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    @Lob
    @Column(name = "cover_art_data", columnDefinition = "BLOB")
    private byte[] coverArtData;

    @Column(name = "cover_art_content_type")
    private String coverArtContentType;

    public String getCoverArtUrl() {
        return (this.coverArtData != null) ? "/api/media/album/" + this.id + "/cover" : null;
    }

    @OneToMany(mappedBy = "album", fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    @com.fasterxml.jackson.annotation.JsonIgnore
    private java.util.List<Song> songs = new java.util.ArrayList<>();

    public int getTotalDuration() {
        return songs.stream().mapToInt(Song::getDuration).sum();
    }

    public String getGenre() {
        if (songs.isEmpty())
            return "Unknown";
        return songs.get(0).getGenre(); 
    }

    @ManyToOne
    @JoinColumn(name = "artist_id", nullable = false)
    @ToString.Exclude
    @com.fasterxml.jackson.annotation.JsonIgnore
    private User artist;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}