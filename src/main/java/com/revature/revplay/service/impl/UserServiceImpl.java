package com.revature.revplay.service.impl;

import com.revature.revplay.dto.UserDto;
import com.revature.revplay.entity.ArtistProfile;
import com.revature.revplay.entity.Role;
import com.revature.revplay.entity.User;
import com.revature.revplay.repository.ArtistProfileRepository;
import com.revature.revplay.repository.PlaylistRepository;
import com.revature.revplay.repository.UserRepository;
import com.revature.revplay.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * This class provides the concrete implementation for the UserService
 * interface.
 * it manages the heavy lifting for user-related data processing, stats
 * calculation,
 * and media updates. By using the @Service annotation, we tell Spring that this
 * class contains the core business logic of our application. It acts as the
 * "brain" for user management, handling everything from basic profile lookups
 * to complex artist dashboard synchronization and password security.
 */
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ArtistProfileRepository artistProfileRepository;
    private final PlaylistRepository playlistRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Constructor used to wire up essential repositories and security components.
     * 
     * The dependencies injected here provide:
     * 1. Direct access to the master 'User' database table.
     * 2. Specialized access to the 'ArtistProfile' details for musician accounts.
     * 3. The ability to query the 'Playlist' system to calculate user stats.
     * 4. A secure tool (passwordEncoder) for transforming passwords into safe
     * hashes.
     * 5. This setup ensures that the service layer has all the "tools" it needs.
     */
    public UserServiceImpl(UserRepository userRepository, ArtistProfileRepository artistProfileRepository,
            PlaylistRepository playlistRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.artistProfileRepository = artistProfileRepository;
        this.playlistRepository = playlistRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Efficiently fetches a User entity by their username or crashes gracefully.
     * 
     * The process for finding a user involves:
     * 1. Querying the UserRepository using a secure, JPA-backed query.
     * 2. Using an 'Optional' container to handle cases where a user might not
     * exist.
     * 3. Throwing a RuntimeException with a clear message if the lookup fails.
     * 4. This method acts as a foundational helper used by almost every other
     * method in this class.
     * 5. It ensures that we always work with valid, database-verified user records.
     */
    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    /**
     * Compiles a comprehensive UserDto containing profile data and engagement
     * stats.
     * 
     * This complex data aggregation method performs:
     * 1. Retrieval of basic textual data like name, bio, and email.
     * 2. If the user is an ARTIST, it fetches specialized info like social links
     * and genres.
     * 3. It calculates total playlist counts by querying the playlist system.
     * 4. It pulls the count of liked songs and following counts from the user's
     * social graph.
     * 5. The @Transactional(readOnly = true) ensures this massive query is fast and
     * safe.
     * 6. The resulting object is a complete "snapshot" of the user used for
     * rendering profiles.
     */
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
                dto.setBio(profile.getBio()); // Artist bio overrides general user bio if needed
                dto.setInstagramUrl(profile.getInstagramUrl());
                dto.setTwitterUrl(profile.getTwitterUrl());
                dto.setYoutubeUrl(profile.getYoutubeUrl());
                dto.setSpotifyUrl(profile.getSpotifyUrl());
                dto.setWebsiteUrl(profile.getWebsiteUrl());
            }
        }

        // Stats for All Users
        dto.setTotalPlaylists((long) playlistRepository.findByUser_Username(username).size());
        dto.setFavoriteSongsCount((long) user.getLikedSongs().size());
        dto.setFollowingCount((long) user.getFollowing().size());
        dto.setListeningTime(0L);

        return dto;
    }

    /**
     * Persists updates to a user's textual info and binary media files.
     * 
     * The multi-step update logic includes:
     * 1. Updating the global user record with new display names or general bios.
     * 2. Processing and storing profile picture byte data if a new file was
     * uploaded.
     * 3. Detecting if the user is an artist and updating their specialized
     * 'ArtistProfile'.
     * 4. Dynamically mapping external social media and website links.
     * 5. Handling high-resolution banner images for artist-tier profiles.
     * 6. This method ensures that the user's presence across the whole app stays
     * up-to-date.
     */
    @Override
    @Transactional
    public void updateUserProfile(String username, UserDto userDto, MultipartFile profilePic, MultipartFile bannerPic) {
        User user = getUserByUsername(username);

        if (userDto.getDisplayName() != null && !userDto.getDisplayName().isEmpty()) {
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
            } catch (IOException e) {
                throw new RuntimeException("Failed to store profile picture in database", e);
            }
        }

        userRepository.save(user);

        if (user.getRole() == Role.ARTIST) {
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
                    // Also update profile pic in artist profile if needed, though they usually sync
                    profile.setProfilePictureData(user.getProfilePictureData());
                    profile.setProfilePictureContentType(user.getProfilePictureContentType());
                } catch (IOException e) {
                    throw new RuntimeException("Failed to store banner in database", e);
                }
            }
            artistProfileRepository.save(profile);
        }
    }

    /**
     * Verifies account ownership during recovery by checking both username and
     * email.
     * 
     * This security check provides:
     * 1. A double-blind verification that the requester knows both identifying
     * marks.
     * 2. Protection against simple username-guessing attacks.
     * 3. A boolean confirmation used to unlock the next stage of the reset process.
     * 4. Integration with the database to confirm the record exists.
     */
    @Override
    public boolean verifyUser(String username, String email) {
        return userRepository.findByUsernameAndEmail(username, email).isPresent();
    }

    /**
     * Finalizes the password reset process with secure hashing.
     * 
     * The password update logic ensures:
     * 1. The raw password never touches the database.
     * 2. The encoder transforms the text into a secure BCrypt string.
     * 3. The user record is saved immediately to prevent session artifacts.
     * 4. After this call, the user can immediately log in with their new
     * credentials.
     * 5. An essential part of maintaining a secure platform.
     */
    @Override
    @Transactional
    public void updatePassword(String username, String newPassword) {
        User user = getUserByUsername(username);
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }
}