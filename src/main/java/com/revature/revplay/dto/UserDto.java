package com.revature.revplay.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * This Data Transfer Object (DTO) is a versatile container used to transport
 * user-related data across the application.
 * It is primarily used for displaying profile information, handling multi-step
 * profile updates, and providing
 * a sanitized view of a user's account details to the front-end. By separating
 * the database entity from this DTO,
 * we ensure that sensitive fields like hashed passwords are never accidentally
 * exposed to the browser.
 * It accommodates both standard 'USER' metrics and professional 'ARTIST'
 * discography data in a single package.
 * It acts as the primary data model for rendering the "Your Library" and
 * "Public Profile" sections.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    /**
     * Core identification fields used to link this DTO to the persistent database
     * record.
     * These identify who the user is uniquely within the revitalization network.
     */
    private Long id;
    private String username;
    private String email;

    /**
     * Profile customization fields that allow users to brand their own space.
     * This includes their chosen public display name, personal bio, and visual
     * avatar.
     */
    private String displayName;
    private String bio;
    private String profilePictureUrl;
    private String role;

    /**
     * Artist-specific branding and external social media links.
     * These fields are populated only when the user has the 'ARTIST' role and a
     * project dashboard.
     * They help music creators drive traffic to their other platforms and
     * professional websites.
     */
    private String artistName;
    private String genre;
    private String bannerImageUrl;
    private String instagramUrl;
    private String twitterUrl;
    private String youtubeUrl;
    private String spotifyUrl;
    private String websiteUrl;

    /**
     * Real-time metadata and engagement statistics pulled from the social graph.
     * These counters allow the user to see their impact on the platform, including
     * the number of lists they've created and the community they follow.
     */
    private Long totalPlaylists;
    private Long favoriteSongsCount;
    private Long listeningTime; // in minutes maybe
    private Long followingCount;
}
