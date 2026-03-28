package com.revature.revplay.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "USERS_SEQ", allocationSize = 1)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "security_question")
    private String securityQuestion;

    @Column(name = "security_answer")
    private String securityAnswer;

    @Column(name = "security_hint")
    private String securityHint;

    @Column(name = "display_name")
    private String displayName;

    @Column(length = 1000)
    private String bio;

    @Lob
    @Column(name = "profile_picture_data", columnDefinition = "BLOB")
    private byte[] profilePictureData;

    @Column(name = "profile_picture_content_type")
    private String profilePictureContentType;

    public String getProfilePictureUrl() {
        return (this.profilePictureData != null) ? "/api/media/user/" + this.id + "/picture" : null;
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @ToString.Exclude
    @com.fasterxml.jackson.annotation.JsonIgnore
    private ArtistProfile artistProfile;

    @OneToMany(mappedBy = "artist", fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    @com.fasterxml.jackson.annotation.JsonIgnore
    private java.util.List<Song> songs = new java.util.ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_liked_songs", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "song_id"))
    @ToString.Exclude
    @Builder.Default
    private java.util.Set<Song> likedSongs = new java.util.HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_following_artists", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "artist_id"))
    @ToString.Exclude
    @Builder.Default
    @com.fasterxml.jackson.annotation.JsonIgnore
    private java.util.Set<User> following = new java.util.HashSet<>();

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (displayName == null) {
            displayName = username;
        }
    }
}