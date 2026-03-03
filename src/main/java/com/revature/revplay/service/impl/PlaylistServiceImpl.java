package com.revature.revplay.service.impl;

import com.revature.revplay.dto.PlaylistDto;
import com.revature.revplay.entity.Playlist;
import com.revature.revplay.entity.Song;
import com.revature.revplay.entity.User;
import com.revature.revplay.exception.ResourceNotFoundException;
import com.revature.revplay.repository.PlaylistRepository;
import com.revature.revplay.repository.SongRepository;
import com.revature.revplay.repository.UserRepository;
import com.revature.revplay.service.PlaylistService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

/**
 * This service implementation contains the core logic for managing custom music
 * collections.
 * It handles the creation, organization, and sharing of user-defined playlists.
 * Beyond simple CRUD operations, this class manages the complex relational
 * links between
 * users, tracks, and the "Liked Songs" social graph.
 * By using the @Service annotation, it is integrated into the Spring context
 * for
 * efficient dependency management and database transaction control.
 */
@Service
public class PlaylistServiceImpl implements PlaylistService {

    private final PlaylistRepository playlistRepository;
    private final SongRepository songRepository;
    private final UserRepository userRepository;

    /**
     * Standard constructor for injecting core repositories.
     * 
     * The dependencies provided here allow the service to:
     * 1. Access and persist high-level playlist records.
     * 2. Linking individual song entities to specific collections.
     * 3. Managing the social 'Likes' relationship stored in user records.
     * 4. Ensuring that all playlist actions are synchronized across the data layer.
     * 5. This setup provides the "glue" that binds users to their musical tastes.
     */
    public PlaylistServiceImpl(PlaylistRepository playlistRepository, SongRepository songRepository,
            UserRepository userRepository) {
        this.playlistRepository = playlistRepository;
        this.songRepository = songRepository;
        this.userRepository = userRepository;
    }

    /**
     * Initializes a brand-new playlist record for the authenticated user.
     * 
     * The creation logic performs the following:
     * 1. Verifying that the owner's user account actually exists in the database.
     * 2. Mapping the DTO metadata (name, description, visibility) to a new Entity.
     * 3. Setting the owner relationship to ensure the user can manage it later.
     * 4. Persisting the record and returning the saved version for immediate UI
     * feedback.
     * 5. This method is the starting point for users wanting to organize their
     * library.
     */
    @Override
    @Transactional
    public Playlist createPlaylist(PlaylistDto playlistDto, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Playlist playlist = Playlist.builder()
                .name(playlistDto.getName())
                .description(playlistDto.getDescription())
                .isPublic(playlistDto.isPublic())
                .user(user)
                .build();

        return playlistRepository.save(playlist);
    }

    /**
     * Fetches a single playlist by ID and ensures all related songs are loaded
     * safely.
     * 
     * The specialized retrieval process involves:
     * 1. Using a helper method to find the record or throw a standard 404 error.
     * 2. Manually triggering the "lazy load" for the associated song collection.
     * 3. This ensures that the caller gets a fully hydrated object with all tracks
     * ready.
     * 4. It acts as the data provider for the main playlist detail view on the
     * platform.
     * 5. It handles complex JPA mapping transitions smoothly within a read-only
     * transaction.
     */
    @Override
    @Transactional(readOnly = true)
    public Playlist getPlaylistById(Long id) {
        Playlist playlist = findPlaylistOrThrow(id);
        playlist.getSongs().size(); // Trigger lazy load
        return playlist;
    }

    /**
     * A private utility for finding a playlist or crashing with a clear error.
     * 
     * This internal helper is used across the service to:
     * 1. Centralize the id-based lookup logic.
     * 2. Consistently throw ResourceNotFoundException for missing items.
     * 3. Reduce code duplication in update, delete, and track management methods.
     * 4. Simplify the entry points of public service methods.
     */
    private Playlist findPlaylistOrThrow(Long id) {
        return playlistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));
    }

    /**
     * Retrieves all collections belonging to a specific user name.
     * 
     * This query is essential for:
     * 1. Rendering the user's sidebar and personal library sections.
     * 2. Providing a list for the "Add to Playlist" drop-down in the music player.
     * 3. Powering the profile page where others can see the user's creations.
     * 4. It is a lightweight query that pulls essential metadata for navigation.
     */
    @Override
    public List<Playlist> getUserPlaylists(String username) {
        return playlistRepository.findByUser_Username(username);
    }

    /**
     * Fetches a list of every playlist that is flag as public to the community.
     * 
     * The social discovery process involves:
     * 1. Querying the database for all records where 'isPublic' is true.
     * 2. Hydrating each playlist's song list for a complete preview.
     * 3. This method is the heart of the "Discovery" and "Global Charts" features.
     * 4. It allows anonymous or registered users to explore curated music from
     * others.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Playlist> getAllPublicPlaylists() {
        List<Playlist> playlists = playlistRepository.findByIsPublicTrue();
        playlists.forEach(p -> p.getSongs().size()); // Trigger lazy load
        return playlists;
    }

    /**
     * Updates an existing playlist's information while enforcing owner security.
     * 
     * The update workflow ensures:
     * 1. Security: It checks that the current requester is the actual owner of the
     * list.
     * 2. Validity: It uses the DTO to update fields like name, bio, and privacy
     * flags.
     * 3. Persistence: It saves the changes back to the master database.
     * 4. Integrity: No tracks are removed or added during this specific meta-data
     * update.
     * 5. It provides a way for users to refine the branding of their music
     * collections.
     */
    @Override
    @Transactional
    public Playlist updatePlaylist(Long id, PlaylistDto playlistDto, String username) {
        Playlist playlist = findPlaylistOrThrow(id);
        if (!playlist.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized");
        }

        playlist.setName(playlistDto.getName());
        playlist.setDescription(playlistDto.getDescription());
        playlist.setPublic(playlistDto.isPublic());

        return playlistRepository.save(playlist);
    }

    /**
     * Removes a playlist permanently after verification of account rights.
     * 
     * The deletion sequence manages:
     * 1. Fetching the playlist record and its owner details.
     * 2. Verifying that the user attempting the delete is the owner.
     * 3. Executing the database delete call to remove the record and its metadata.
     * 4. This clean removal ensures no dead links remain in the system.
     */
    @Override
    @Transactional
    public void deletePlaylist(Long id, String username) {
        Playlist playlist = findPlaylistOrThrow(id);
        if (!playlist.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized");
        }
        playlistRepository.delete(playlist);
    }

    /**
     * Adds a specific track to a user's collection while managing relationship
     * integrity.
     * 
     * The track addition process follows these steps:
     * 1. Loading the playlist and song records from their respective repositories.
     * 2. Ensuring the requesting user has the right to modify the playlist.
     * 3. Initializing the track collection safely if it hasn't been loaded yet
     * (Lazy Init).
     * 4. Adding the song and persisting the change to the JPA collection.
     * 5. This method enables the primary curation feature of the library system.
     */
    @Override
    @Transactional
    public Playlist addSongToPlaylist(Long playlistId, Long songId, String username) {
        Playlist playlist = findPlaylistOrThrow(playlistId);
        playlist.getSongs().size(); // init lazy collection
        if (!playlist.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized");
        }

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));

        playlist.getSongs().add(song);
        return playlistRepository.save(playlist);
    }

    /**
     * Removes a particular track from a playlist without affecting the original
     * song record.
     * 
     * The removal logic safely handles:
     * 1. Verifying identity and permission for the person editing the list.
     * 2. Finding the song within the playlist's track set.
     * 3. Removing the link from the join table via the JPA collection manager.
     * 4. This allows users to keep their playlists precise and up-to-date.
     */
    @Override
    @Transactional
    public Playlist removeSongFromPlaylist(Long playlistId, Long songId, String username) {
        Playlist playlist = findPlaylistOrThrow(playlistId);
        playlist.getSongs().size(); // init lazy collection
        if (!playlist.getUser().getUsername().equals(username)) {
            throw new RuntimeException("Unauthorized");
        }

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));

        playlist.getSongs().remove(song);
        return playlistRepository.save(playlist);
    }

    /**
     * Manages the "Like" button logic by adding or removing songs from favorites.
     * 
     * This social feature aggregates several actions:
     * 1. Checking the current "Like" status of a song for a specific user.
     * 2. If already liked, it performs an 'Unlike' by removing it from the user's
     * set.
     * 3. If not yet liked, it performs a 'Like' by adding it to their personal
     * collection.
     * 4. It returns the new state (true for liked, false for unliked) for UI
     * updates.
     * 5. This method is the primary driver of the user's favorites list.
     */
    @Override
    @Transactional
    public boolean toggleLikeSong(Long songId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));

        user.getLikedSongs().size(); // Trigger lazy load
        boolean isLiked = user.getLikedSongs().contains(song);
        if (isLiked) {
            user.getLikedSongs().remove(song);
        } else {
            user.getLikedSongs().add(song);
        }
        userRepository.save(user);
        return !isLiked;
    }

    /**
     * Fetches the entire collection of songs that a user has highlighted as a
     * favorite.
     * 
     * The favorites loading procedure ensures:
     * 1. All track metadata is loaded within a safe transaction context.
     * 2. Lazy relationships are properly triggered to avoid data loss.
     * 3. The resulting set is ready for shuffle-play or full-list listening.
     * 4. It acts as the data provider for the "Liked Songs" section of the library.
     */
    @Override
    @Transactional(readOnly = true)
    public Set<Song> getLikedSongs(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        // trigger lazy load inside transaction
        user.getLikedSongs().size();
        return user.getLikedSongs();
    }

    /**
     * Performs a fast check to see if a song is already present in a user's
     * library.
     * 
     * This utility check provides:
     * 1. Real-time feedback for the 'Heart' icon in the music player.
     * 2. A way for the UI to show 'Add' vs 'Remove' options on song menus.
     * 3. Snappy performance by working with cached user metadata when possible.
     * 4. It ensures the user always knows their relationship with a specific track.
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isSongLiked(Long songId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found"));
        user.getLikedSongs().size(); // trigger lazy load
        return user.getLikedSongs().contains(song);
    }
}
