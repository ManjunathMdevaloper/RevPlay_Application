package com.revature.revplay.service;

import com.revature.revplay.dto.PlaylistDto;
import com.revature.revplay.entity.Playlist;
import com.revature.revplay.entity.Song;

import java.util.List;
import java.util.Set;

/**
 * This interface defines the contract for all playlist management and social
 * music features.
 * It manages the lifecycle of custom user collections, including creating,
 * sharing, and
 * organizing tracks. Beyond just playlists, this service also handles the
 * "Liked Songs"
 * logic, which is a core part of the user's personal music library.
 * It acts as the orchestration layer that connects users to the songs they love
 * and want
 * to organize into groups.
 */
public interface PlaylistService {

    /**
     * Creates a new playlist for a specific user.
     * 
     * The creation process involve:
     * 1. Receiving metadata about the playlist (name, description, visibility).
     * 2. Finding the user account that will own this collection.
     * 3. Initializing an empty set of songs and setting the creation timestamp.
     * 4. Persisting the new playlist to the database so it appears in the user's
     * sidebar.
     * 5. Ensuring that the playlist is properly linked to its creator for security.
     */
    Playlist createPlaylist(PlaylistDto playlistDto, String username);

    /**
     * Retrieves a single playlist by its unique identification number.
     * 
     * This lookup method is vital for:
     * 1. Loading the individual playlist pages where users can see all tracks.
     * 2. Providing data for the music player when a user starts playing a whole
     * list.
     * 3. Checking the existence of a playlist before performing updates or
     * deletions.
     * 4. It ensures the system can find and display any shared or private
     * collection.
     */
    Playlist getPlaylistById(Long id);

    /**
     * Fetches all playlists owned by a specific user.
     * 
     * This library query is used to:
     * 1. Populate the "My Library" section for the logged-in user.
     * 2. Display a list of options when a user wants to add a song to one of their
     * lists.
     * 3. Filter the collections to ensure users only see what they have created.
     * 4. It provides the essential navigation data for the user's personal profile.
     */
    List<Playlist> getUserPlaylists(String username);

    /**
     * Retrieves a list of all playlists that have been marked as 'Public' by their
     * owners.
     * 
     * This social feature enables:
     * 1. Community discovery where users can find music curated by others.
     * 2. Populating the 'Discovery' page with trending or popular lists.
     * 3. Encouraging user engagement by allowing them to browse public taste
     * profiles.
     * 4. Filtered access that respects privacy settings in the database.
     */
    List<Playlist> getAllPublicPlaylists();

    /**
     * Modifies the metadata of an existing playlist, such as its title or
     * visibility.
     * 
     * The update logic enforces:
     * 1. Ownership security: It verifies the requester is actually the owner of the
     * list.
     * 2. Data integrity: It updates fields like description while keeping track
     * lists intact.
     * 3. Privacy control: It allows users to toggle their collections between
     * public and private.
     * 4. Immediate feedback: It returns the updated entity for instant UI refresh.
     */
    Playlist updatePlaylist(Long id, PlaylistDto playlistDto, String username);

    /**
     * Permanently deletes a playlist from the user's library.
     * 
     * The deletion sequence includes:
     * 1. Confirming the requester has the correct authority (is the owner).
     * 2. Severing the relationship between the playlist and its contained songs.
     * 3. Removing the record from the database to clean up the user's view.
     * 4. This operation is irreversible and ensures the library remains tidy.
     */
    void deletePlaylist(Long id, String username);

    /**
     * Adds a specific song to a user's chosen playlist.
     * 
     * This organizational method handles:
     * 1. Validating that both the playlist and the song exist in the system.
     * 2. Checking if the user has permission to modify the target playlist.
     * 3. Ensuring no duplicates are added (if that is the business rule).
     * 4. Saving the link in the join table to keep the track list updated.
     */
    Playlist addSongToPlaylist(Long playlistId, Long songId, String username);

    /**
     * Removes a track from a playlist without deleting the song itself.
     * 
     * The removal process involves:
     * 1. Identifying the specific track within the playlist's collection.
     * 2. Verifying the owner's permission to edit the playlist.
     * 3. Updating the relational link in the database.
     * 4. This allows users to refine and curate their lists over time.
     */
    Playlist removeSongFromPlaylist(Long playlistId, Long songId, String username);

    /**
     * Toggles a song's status in the user's "Liked Songs" (Favorites) collection.
     * 
     * This social feature is crucial because:
     * 1. It acts as a primary "bookmark" for songs a user wants to hear again.
     * 2. It handles both 'liking' (adding) and 'unliking' (removing) in a single
     * call.
     * 3. It provides data for the 'Like' button state (filled vs. outline heart).
     * 4. It feeds into the global popularity ranking for search and discovery.
     */
    boolean toggleLikeSong(Long songId, String username);

    /**
     * Retrieves the entire set of songs that a user has marked as favorites.
     * 
     * This personal library query:
     * 1. Powers the "Liked Songs" special playlist view.
     * 2. Allows the user to quickly access their most-listened-to tracks.
     * 3. Provides context for the music player's 'Shuffle' and 'Smart Radio'
     * features.
     */
    Set<Song> getLikedSongs(String username);

    /**
     * A quick check to see if a specific song is currently liked by a user.
     * 
     * This utility method is used to:
     * 1. Update the UI heart icon state when a song starts playing.
     * 2. Prevent redundant database operations for liking/unliking.
     * 3. Provide a snappy user experience by knowing the state of a song's
     * popularity instantly.
     */
    boolean isSongLiked(Long songId, String username);
}