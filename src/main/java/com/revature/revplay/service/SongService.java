package com.revature.revplay.service;

import com.revature.revplay.entity.Song;

import java.util.List;

/**
 * This interface outlines all music-related business logic and track management
 * operations.
 * It serves as the primary gateway for interacting with individual songs, from
 * basic metadata
 * retrieval to complex file handling and play-tracking analytics.
 * This contract ensures that any implementation provides a consistent way to
 * handle
 * the massive amount of audio data and cover art that flows through the system
 * daily.
 */
public interface SongService {

        /**
         * Retrieves a complete list of every song currently available on the platform.
         * 
         * This method is essential for:
         * 1. Powering the global discovery page where users find new music.
         * 2. Providing a baseline data set for search and filtering features.
         * 3. Allowing administrators to oversee the content library.
         * 4. Ensuring that newest releases can be highlighted to the community.
         */

List<Song> getAllSongs();



        /**
         * Orchestrates the complex task of uploading and saving a new musical track.
         *
         * The save process involves:
         * 1. Parsing the incoming SongDto for titling and genre metadata.
         * 2. Securely processing a large Multpart audio file (e.g., MP3 or WAV).
         * 3. Converting and storing the cover art image alongside the track data.
         * 4. Linking the new song to the specific User who uploaded it (the Artist).
         * 5. This method is the "doorway" for new music entering the RevPlay ecosystem.
         */
        Song saveSong(com.revature.revplay.dto.SongDto songDto, com.revature.revplay.entity.User artist,
                      org.springframework.web.multipart.MultipartFile audioFile,
                      org.springframework.web.multipart.MultipartFile coverFile);

        /**
         * Modifies the details or visual art of an existing song in the library.
         *
         * Update logic ensures that:
         * 1. Only the original creator (ArtistId) has the authority to change the
         * track.
         * 2. New cover art can be uploaded specifically to refresh the song's look.
         * 3. Title or genre changes are reflected immediately across the user's
         * playlists.
         * 4. Media data is properly replaced without leaving "ghost" files in the
         * database.
         */
        Song updateSong(Long id, com.revature.revplay.dto.SongDto songDto, Long artistId,
                        org.springframework.web.multipart.MultipartFile coverFile);

        /**
         * A direct save method for internal song entity persistence.
         *
         * This specialized method is used for:
         * 1. Rapidly persisting changes to a song entity that has already been modified
         * in memory.
         * 2. Saving system-level updates like 'play counts' or 'rating' averages.
         * 3. It bypasses the file-upload overhead for purely metadata-based updates.
         */
        Song saveSong(Song song);

        /**
         * Permanently removes a song and all its associated media from the platform.
         *
         * The deletion sequence includes:
         * 1. Verifying that the requester is actually the owner of the track.
         * 2. Removing entries from the 'Liked Songs' and 'Playlists' of every user who
         * has it.
         * 3. Finally, deleting the actual binary data from the storage layer.
         * 4. This ensures a clean removal without breaking other parts of the system.
         */
        void deleteSong(Long id, Long artistId);


}
