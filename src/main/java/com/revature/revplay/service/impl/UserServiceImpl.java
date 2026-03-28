package com.revature.revplay.service.impl;

import com.revature.revplay.dto.UserDto;
import com.revature.revplay.entity.ArtistProfile;
import com.revature.revplay.entity.Role;
import com.revature.revplay.entity.User;
import com.revature.revplay.repository.ArtistProfileRepository;
import com.revature.revplay.repository.PlaylistRepository;
import com.revature.revplay.repository.UserRepository;
import com.revature.revplay.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;


@Service
@Log4j2
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ArtistProfileRepository artistProfileRepository;
    private final PlaylistRepository playlistRepository;
    private final PasswordEncoder passwordEncoder;

    
    public UserServiceImpl(UserRepository userRepository, ArtistProfileRepository artistProfileRepository,
            PlaylistRepository playlistRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.artistProfileRepository = artistProfileRepository;
        this.playlistRepository = playlistRepository;
        this.passwordEncoder = passwordEncoder;
    }

    
    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    @Override
    public User getUserByUsernameOrEmail(String identifier) {
        return userRepository.findByUsernameOrEmail(identifier, identifier)
                .orElseThrow(() -> new RuntimeException("User not found with username or email: " + identifier));
    }

    
    @Override
    @Transactional(readOnly = true)
    public UserDto getUserProfile(String username) {
        User user = getUserByUsername(username);
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        dto.setDisplayName(user.getDisplayName());
        dto.setBio(user.getBio());
        dto.setProfilePictureUrl(user.getProfilePictureUrl());
        dto.setRole(user.getRole().name());

        if (user.getRole() == Role.ARTIST) {
            ArtistProfile profile = artistProfileRepository.findById(user.getId()).orElse(null);
            if (profile != null) {
                dto.setArtistName(profile.getArtistName());
                dto.setGenre(profile.getGenre());
                dto.setBannerImageUrl(profile.getBannerImageUrl());
                dto.setBio(profile.getBio()); 
                dto.setInstagramUrl(profile.getInstagramUrl());
                dto.setTwitterUrl(profile.getTwitterUrl());
                dto.setYoutubeUrl(profile.getYoutubeUrl());
                dto.setSpotifyUrl(profile.getSpotifyUrl());
                dto.setWebsiteUrl(profile.getWebsiteUrl());
            }
        }

        
        dto.setTotalPlaylists((long) playlistRepository.findByUser_Username(username).size());
        dto.setFavoriteSongsCount((long) user.getLikedSongs().size());
        dto.setFollowingCount((long) user.getFollowing().size());
        dto.setListeningTime(0L);

        
        if (user.getFollowing() != null) {
            dto.setFollowedArtists(user.getFollowing().stream().map(followed -> {
                UserDto simpleDto = new UserDto();
                simpleDto.setId(followed.getId());
                simpleDto.setUsername(followed.getUsername());
                simpleDto.setDisplayName(followed.getDisplayName());
                simpleDto.setProfilePictureUrl(followed.getProfilePictureUrl());
                if (followed.getRole() == Role.ARTIST && followed.getArtistProfile() != null) {
                    simpleDto.setArtistName(followed.getArtistProfile().getArtistName());
                    simpleDto.setGenre(followed.getArtistProfile().getGenre());
                }
                return simpleDto;
            }).collect(java.util.stream.Collectors.toList()));
        }

        return dto;
    }

    
    @Override
    @Transactional
    public void updateUserProfile(String username, UserDto userDto, MultipartFile profilePic, MultipartFile bannerPic) {
        log.info("Updating profile for user: {}", username);
        User user = getUserByUsername(username);

        if (userDto.getDisplayName() != null && !userDto.getDisplayName().isEmpty()) {
            log.debug("Updating display name for {}: {}", username, userDto.getDisplayName());
            user.setDisplayName(userDto.getDisplayName());
        }

        if (user.getRole() == Role.USER) {
            if (userDto.getBio() != null) {
                user.setBio(userDto.getBio());
            }
        }

        if (profilePic != null && !profilePic.isEmpty()) {
            try {
                user.setProfilePictureData(profilePic.getBytes());
                user.setProfilePictureContentType(profilePic.getContentType());
                log.info("Profile picture updated for user: {}", username);
            } catch (IOException e) {
                log.error("Failed to byte-read profile picture for user {}", username, e);
                throw new RuntimeException("Failed to store profile picture in database", e);
            }
        }

        userRepository.save(user);

        if (user.getRole() == Role.ARTIST) {
            log.info("Syncing artist profile updates for user: {}", username);
            ArtistProfile profile = artistProfileRepository.findById(user.getId())
                    .orElse(new ArtistProfile());
            profile.setUser(user);
            if (userDto.getArtistName() != null && !userDto.getArtistName().isEmpty()) {
                profile.setArtistName(userDto.getArtistName());
            }
            if (userDto.getBio() != null) {
                profile.setBio(userDto.getBio());
            }
            if (userDto.getGenre() != null) {
                profile.setGenre(userDto.getGenre());
            }
            if (userDto.getInstagramUrl() != null) {
                profile.setInstagramUrl(userDto.getInstagramUrl());
            }
            if (userDto.getTwitterUrl() != null) {
                profile.setTwitterUrl(userDto.getTwitterUrl());
            }
            if (userDto.getYoutubeUrl() != null) {
                profile.setYoutubeUrl(userDto.getYoutubeUrl());
            }
            if (userDto.getSpotifyUrl() != null) {
                profile.setSpotifyUrl(userDto.getSpotifyUrl());
            }
            if (userDto.getWebsiteUrl() != null) {
                profile.setWebsiteUrl(userDto.getWebsiteUrl());
            }
            if (bannerPic != null && !bannerPic.isEmpty()) {
                try {
                    profile.setBannerImageData(bannerPic.getBytes());
                    profile.setBannerImageContentType(bannerPic.getContentType());
                    
                    profile.setProfilePictureData(user.getProfilePictureData());
                    profile.setProfilePictureContentType(user.getProfilePictureContentType());
                    log.info("Artist banner graphic updated for user: {}", username);
                } catch (IOException e) {
                    log.error("Failed to byte-read banner context for user {}", username, e);
                    throw new RuntimeException("Failed to store banner in database", e);
                }
            }
            artistProfileRepository.save(profile);
            log.debug("Artist profile persisted for {}", username);
        }
    }

    
    @Override
    public boolean verifyUser(String username, String email) {
        return userRepository.findByUsernameAndEmail(username, email).isPresent();
    }

    
    @Override
    @Transactional
    public void updatePassword(String username, String newPassword) {
        log.info("Updating password for user: {}", username);
        User user = getUserByUsername(username);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password successfully updated and hashed for user: {}", username);
    }
}