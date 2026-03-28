package com.revature.revplay.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "artist_profiles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ArtistProfile {

    @Id
    private Long id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private User user;

    @Column(name = "artist_name")
    private String artistName;

    @Column(length = 2000)
    private String bio;

    private String genre;

    @Lob
    @Column(name = "profile_picture_data", columnDefinition = "BLOB")
    private byte[] profilePictureData;

    @Column(name = "profile_picture_content_type")
    private String profilePictureContentType;

    @Lob
    @Column(name = "banner_image_data", columnDefinition = "BLOB")
    private byte[] bannerImageData;

    @Column(name = "banner_image_content_type")
    private String bannerImageContentType;

    public String getProfilePictureUrl() {
        return (this.profilePictureData != null) ? "/api/media/artist/" + this.id + "/picture" : null;
    }

    public String getBannerImageUrl() {
        return (this.bannerImageData != null) ? "/api/media/artist/" + this.id + "/banner" : null;
    }

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