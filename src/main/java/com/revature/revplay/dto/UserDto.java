package com.revature.revplay.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    
    private Long id;
    private String username;
    private String email;

    
    private String displayName;
    private String bio;
    private String profilePictureUrl;
    private String role;

    
    private String artistName;
    private String genre;
    private String bannerImageUrl;
    private String instagramUrl;
    private String twitterUrl;
    private String youtubeUrl;
    private String spotifyUrl;
    private String websiteUrl;

    
    private Long totalPlaylists;
    private Long favoriteSongsCount;
    private Long listeningTime; 
    private Long followingCount;
    private java.util.List<UserDto> followedArtists;
}