package com.revature.revplay.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * This JPA entity extends the base User record for members with the 'ARTIST'
 * role.
 * It houses specialized professional metadata that defines a creator's public
 * persona.
 * This includes high-resolution banner images, a professional stage name,
 * detailed creative biographies, and direct links to their external social
 * media presence.
 * By using a One-to-One mapping with Shared ID, it ensures that every artist
 * profile is strictly tethered to a valid user account while keeping the
 * base User table lightweight for standard listeners.
 */
@Entity
@Table(name = "artist_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtistProfile {

    /**
     * The primary key for the profile, which is mapped directly from the User's ID.
     * This shared identifier pattern (MapsId) ensures 1:1 integrity between
     * the account and the professional profile.
     */
    @Id
    private Long id;

    /**
     * The underlying User account that owns this professional profile project.
     */
    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    /**
     * The professional stage name or brand used by the artist for public
     * recognition.
     */
    @Column(name = "artist_name")
    private String artistName;

    /**
     * A deep, multi-paragraph field for the artist to share their musical journey
     * and vision.
     */
    @Column(length = 2000)
    private String bio;

    /**
     * The primary musical style or movement the artist identifies with for
     * cataloging.
     */
    private String genre;

    /**
     * Binary storage for a professional-grade profile picture or artist logo
     * (BLOB).
     * This allows creators to have distinct branding separate from their personal
     * listener avatar.
     */
    @Lob
    @Column(name = "profile_picture_data", columnDefinition = "BLOB")
    private byte[] profilePictureData;

    @Column(name = "profile_picture_content_type")
    private String profilePictureContentType;

    /**
     * Binary storage for the large horizontal banner image displayed at the top of
     * profiles.
     * This is a key visual element for the "Artist Dashboard" and public creator
     * pages.
     */
    @Lob
    @Column(name = "banner_image_data", columnDefinition = "BLOB")
    private byte[] bannerImageData;

    @Column(name = "banner_image_content_type")
    private String bannerImageContentType;

    /**
     * Generates a virtual web URL for fetching the professional artist thumbnail.
     */
    public String getProfilePictureUrl() {
        return (this.profilePictureData != null) ? "/api/media/artist/" + this.id + "/picture" : null;
    }

    /**
     * Generates a virtual web URL for fetching the wide banner graphic.
     */
    public String getBannerImageUrl() {
        return (this.bannerImageData != null) ? "/api/media/artist/" + this.id + "/banner" : null;
    }

    /**
     * External engagement links used to build an artist's cross-platform presence.
     * These fields allow creators to funnel RevPlay listeners to their other social
     * hubs.
     */
    @Column(name = "instagram_url")
    private String instagramUrl;

    @Column(name = "twitter_url")
    private String twitterUrl;

    @Column(name = "youtube_url")
    private String youtubeUrl;

    @Column(name = "spotify_url")
    private String spotifyUrl;

    @Column(name = "website_url")
    private String websiteUrl;
}