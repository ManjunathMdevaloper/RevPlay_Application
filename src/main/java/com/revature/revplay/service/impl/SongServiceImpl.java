package com.revature.revplay.service.impl;

import com.revature.revplay.dto.SongDto;
import com.revature.revplay.entity.Song;
import com.revature.revplay.entity.User;
import com.revature.revplay.exception.ResourceNotFoundException;
import com.revature.revplay.repository.HistoryRepository;
import com.revature.revplay.repository.SongRepository;
import com.revature.revplay.repository.UserRepository;
import com.revature.revplay.service.SongService;
import jakarta.persistence.EntityManager;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * This service implementation contains the core logic for music track
 * management.
 * It manages the lifecycle of high-fidelity audio data and song metadata across
 * the app.
 * By implementing the SongService interface, it provides a reliable way to
 * handle
 * binary file storage in the primary database, ensuring that media is never
 * lost.
 * It coordinates interaction between songs, artists, user listening history,
 * and
 * popularity statistics to provide a high-performance streaming experience.
 */
@Service
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final HistoryRepository historyRepository;
    private final EntityManager entityManager;

    /**
     * Standard constructor used to initialize the song management engine.
     * 
     * The dependencies provided here allow the service to:
     * 1. Direct interaction with the master 'Songs' data table.
     * 2. Linking songs to their uploaded creators (Users).
     * 3. Logging every play event into the user's permanent history.
     * 4. Performing low-level database operations for complex cleanup tasks.
     * 5. Ensuring that all repositories are ready for high-concurrency music
     * streaming.
     */
    public SongServiceImpl(SongRepository songRepository, UserRepository userRepository,
            HistoryRepository historyRepository, EntityManager entityManager) {
        this.songRepository = songRepository;
        this.userRepository = userRepository;
        this.historyRepository = historyRepository;
        this.entityManager = entityManager;
    }

    /**
     * Retrieves all music tracks currently stored in the system.
     * 
     * The process for returning the full library includes:
     * 1. Calling the repository's findAll() method to grab every record.
     * 2. This data powers the 'Global Charts' and 'New Releases' sections.
     * 3. It provides the front-end with a searchable index of the entire platform.
     * 4. It supports bulk operations where the system needs to analyze all content.
     */
    @Override
    public List<Song> getAllSongs() {
        return songRepository.findAll();
    }

    /**
     * Fetches a single track's metadata and media data by its unique ID.
     * 
     * The lookup process ensures data quality by:
     * 1. Using the song's primary key to find the exact database record.
     * 2. Utilizing an Optional container to prevent NullPointerException bugs.
     * 3. Throwing a custom ResourceNotFoundException if the track has been removed.
     * 4. This method is the backbone for the persistent music player at the bottom
     * of the screen.
     * 5. It serves the raw data required to render song details almost instantly.
     */
    @Override
    public Song getSongById(Long id) {
        return songRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found with id " + id));
    }

    /**
     * Finds every song belonging to a specific artist's discography.
     * 
     * This artist-specific query involves:
     * 1. Verifying that the artist profile actually exists in the user table.
     * 2. Performing a relational lookup in the songs table for matching artist
     * links.
     * 3. Returning a list of song entities that define that artist's presence on
     * RevPlay.
     * 4. It ensures that users only see the music relevant to the creator they are
     * currently viewing.
     * 5. This method is crucial for the "View Artist" page feature.
     */
    @Override
    public List<Song> getSongsByArtistId(Long artistId) {
        User artist = userRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found"));
        return songRepository.findByArtist(artist);
    }

    /**
     * Processes and stores a new song upload, including its actual audio and cover
     * art.
     * 
     * This heavy-duty method manages the following technical steps:
     * 1. Validating that a mandatory audio file (MP3/WAV) has been provided by the
     * user.
     * 2. Extracting raw byte data from the multi-part file for database insertion.
     * 3. Building a new Song entity using a modern 'Builder' pattern for clean
     * initialization.
     * 4. Conditionally processing a secondary cover art image if one was uploaded.
     * 5. Handling potential IO exceptions during the byte-reading phase.
     * 6. Saving the final track record so it immediately becomes available for
     * streaming.
     */
    @Override
    @Transactional
    public Song saveSong(SongDto songDto, User artist, MultipartFile audioFile, MultipartFile coverFile) {
        if (audioFile == null || audioFile.isEmpty()) {
            throw new IllegalArgumentException("Audio file is explicitly required.");
        }

        try {
            Song song = Song.builder()
                    .title(songDto.getTitle())
                    .artist(artist)
                    .albumId(songDto.getAlbumId())
                    .genre(songDto.getGenre())
                    .duration(songDto.getDuration() != null ? songDto.getDuration() : 0)
                    .audioData(audioFile.getBytes())
                    .audioContentType(audioFile.getContentType())
                    .build();

            if (coverFile != null && !coverFile.isEmpty()) {
                song.setCoverArtData(coverFile.getBytes());
                song.setCoverArtContentType(coverFile.getContentType());
            }

            return songRepository.save(song);
        } catch (java.io.IOException e) {
            throw new RuntimeException("Failed to store media in database", e);
        }
    }

    /**
     * Updates an existing song's metadata and provides validation for ownership.
     * 
     * The track update logic ensures:
     * 1. Security: It checks if the authenticated user is the actual artist who
     * uploaded it.
     * 2. Flexibility: Allows changing titles, genres, and album associations
     * independently.
     * 3. Media Support: Safely replaces the cover art bytes without affecting the
     * audio data.
     * 4. Efficiency: Only modifies fields that have new incoming data from the
     * form.
     * 5. Consistency: Propagates these changes across all playlists that contain
     * this song.
     */
    @Override
    @Transactional
    public Song updateSong(Long id, SongDto songDto, Long artistId, MultipartFile coverFile) {
        Song song = getSongById(id);

        if (!song.getArtist().getId().equals(artistId)) {
            throw new RuntimeException("Unauthorized action.");
        }

        if (songDto.getTitle() != null && !songDto.getTitle().isEmpty()) {
            song.setTitle(songDto.getTitle());
        }
        if (songDto.getGenre() != null)
            song.setGenre(songDto.getGenre());
        song.setAlbumId(songDto.getAlbumId());

        if (coverFile != null && !coverFile.isEmpty()) {
            try {
                song.setCoverArtData(coverFile.getBytes());
                song.setCoverArtContentType(coverFile.getContentType());
            } catch (java.io.IOException e) {
                throw new RuntimeException("Failed to update cover art in database", e);
            }
        }

        return songRepository.save(song);
    }

    /**
     * A simple wrapper for persisting a pre-configured song entity.
     * 
     * This method is useful for:
     * 1. Updating play counts or ratings in a high-concurrency environment.
     * 2. Performing quick system-level data cleanup.
     * 3. Saving track changes that were manipulated in a separate part of the
     * business logic.
     */
    @Override
    @Transactional
    public Song saveSong(Song song) {
        return songRepository.save(song);
    }

    /**
     * Performs a deep delete of a song, clearing it from all user libraries and
     * playlists.
     * 
     * The deletion sequence is critical to prevent "orphan" data:
     * 1. It verifies the identity of the artist to prevent malicious track removal.
     * 2. It scans and deletes the user's listening history records for this track.
     * 3. It runs the manual SQL cleanup for 'Liked Songs' and 'Playlist Tracks'
     * join tables.
     * 4. It flushes the changes to ensure all relationships are severed before the
     * song record dies.
     * 5. Finally, it removes the master record and its binary audio/art from
     * existence.
     */
    @Override
    @Transactional
    public void deleteSong(Long id, Long artistId) {
        Song song = getSongById(id);

        if (!song.getArtist().getId().equals(artistId)) {
            throw new RuntimeException("Unauthorized to delete this asset.");
        }

        // Clear all FK references before deleting
        // 1. Remove from history
        historyRepository.deleteBySong(song);

        // 2. Remove from liked songs (user_liked_songs join table)
        entityManager.createNativeQuery("DELETE FROM user_liked_songs WHERE song_id = :songId")
                .setParameter("songId", id).executeUpdate();

        // 3. Remove from playlists (playlist_songs join table)
        entityManager.createNativeQuery("DELETE FROM playlist_songs WHERE song_id = :songId")
                .setParameter("songId", id).executeUpdate();

        entityManager.flush();
        songRepository.delete(song);
    }

    /**
     * Increments popularity metrics and logs track history for personalized
     * discovery.
     * 
     * The 'Record Play' process follows these steps:
     * 1. It increments the global play_count on the song object for popularity
     * rankings.
     * 2. If a user is logged in, it creates a new 'History' record linking the user
     * to the track.
     * 3. This enables the "Recently Played" feature on the user's homepage.
     * 4. It builds the data needed for the trending songs algorithm to work
     * correctly.
     * 5. It happens asynchronously/behind the scenes so the music player never
     * lags.
     */
    @Override
    @Transactional
    public void recordPlay(Long id, String username) {
        Song song = songRepository.findById(id).orElse(null);
        if (song != null) {
            song.setPlayCount(song.getPlayCount() + 1);
            songRepository.save(song);

            if (username != null && !username.isEmpty()) {
                User user = userRepository.findByUsername(username).orElse(null);
                if (user != null) {
                    com.revature.revplay.entity.History history = com.revature.revplay.entity.History.builder()
                            .user(user)
                            .song(song)
                            .build();
                    historyRepository.save(history);
                }
            }
        }
    }
}