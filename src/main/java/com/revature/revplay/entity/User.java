package com.revature.revplay.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * This core JPA entity represents a registered member of the RevPlay platform.
 * It is the primary identity record used for authentication, profile
 * management, and
 * social interactions. The User entity is versatile, capable of representing
 * both
 * a standard 'USER' (listener) and an 'ARTIST' (creator) through a unified role
 * system.
 * It maintains comprehensive links to the user's liked songs, followed artists,
 * and
 * professional music output if they are a creator.
 * As the heart of the security system, it stores essential login credentials
 * and profile branding.
 */
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

    /**
     * The unique database primary key for the user, managed via an Oracle-style
     * sequence.
     * This ID is used throughout the system for relational linking and media URL
     * generation.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "USERS_SEQ", allocationSize = 1)
    @EqualsAndHashCode.Include
    private Long id;

    /**
     * The unique login name and primary contact email for the user account.
     * Both fields are strictly unique to prevent account duplication and ensure
     * security.
     */
    @Column(unique = true, nullable = false)
    private String username;

    @Column(unique = true, nullable = false)
    private String email;

    /**
     * The encrypted (hashed) password secret for the account.
     * This is never stored in plain-text and is managed by BCrypt in the security
     * layer.
     */
    @Column(nullable = false)
    private String password;

    @Column(name = "security_question")
    private String securityQuestion;

    @Column(name = "security_answer")
    private String securityAnswer;

    @Column(name = "security_hint")
    private String securityHint;

    /**
     * Public-facing branding fields including the user's chosen name and creative
     * biography.
     * The display name defaults to the username if not explicitly set by the user.
     */
    @Column(name = "display_name")
    private String displayName;

    @Column(length = 1000)
    private String bio;

    /**
     * Binary storage for the user's avatar image (BLOB).
     * By storing the image bytes directly in the database, we ensure profile data
     * is portable and consistently backed up with the account record.
     */
    @Lob
    @Column(name = "profile_picture_data", columnDefinition = "BLOB")
    private byte[] profilePictureData;

    @Column(name = "profile_picture_content_type")
    private String profilePictureContentType;

    /**
     * Generates a virtual web URL that the browser can use to fetch the user's
     * avatar.
     * This maps to the specialized MediaController which streams the BLOB data.
     */
    public String getProfilePictureUrl() {
        return (this.profilePictureData != null) ? "/api/media/user/" + this.id + "/picture" : null;
    }

    /**
     * Defines the authorization level (USER vs ARTIST) for this account.
     * This enumerated field determines which dashboards and tools the user can
     * access.
     */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    /**
     * Links this user to their professional ArtistProfile if they hold the 'ARTIST'
     * role.
     * This is a one-to-one relationship that houses deeper branding like banners
     * and social links.
     */
    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    @ToString.Exclude
    @com.fasterxml.jackson.annotation.JsonIgnore
    private ArtistProfile artistProfile;

    /**
     * The master catalog of songs uploaded by this user (if they are an artist).
     * This establishes the ownership relationship between creators and their music.
     */
    @OneToMany(mappedBy = "artist", fetch = FetchType.LAZY)
    @Builder.Default
    @ToString.Exclude
    @com.fasterxml.jackson.annotation.JsonIgnore
    private java.util.List<Song> songs = new java.util.ArrayList<>();

    /**
     * A collection of tracks that the user has marked with a heart (Liked).
     * This powers the "Liked Songs" section of the user's library and influences
     * trending.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_liked_songs", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "song_id"))
    @ToString.Exclude
    @Builder.Default
    private java.util.Set<Song> likedSongs = new java.util.HashSet<>();

    /**
     * The social graph representing which artists this user is following.
     * This is a self-referencing many-to-many relationship used for community
     * discovery.
     */
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "user_following_artists", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "artist_id"))
    @ToString.Exclude
    @Builder.Default
    @com.fasterxml.jackson.annotation.JsonIgnore
    private java.util.Set<User> following = new java.util.HashSet<>();

    /**
     * Lifecycle hook that automatically captures the account creation timestamp.
     * It also ensures that a sensible display name exists immediately upon
     * registration.
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        if (displayName == null) {
            displayName = username;
        }
    }
}