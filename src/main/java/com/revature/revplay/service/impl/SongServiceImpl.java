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
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Service
@Log4j2
public class SongServiceImpl implements SongService {

    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final HistoryRepository historyRepository;
    private final EntityManager entityManager;

    
    public SongServiceImpl(SongRepository songRepository, UserRepository userRepository,
            HistoryRepository historyRepository, EntityManager entityManager) {
        this.songRepository = songRepository;
        this.userRepository = userRepository;
        this.historyRepository = historyRepository;
        this.entityManager = entityManager;
    }

    
    @Override
    public List<Song> getAllSongs() {
        return songRepository.findAll();
    }

    
    @Override
    public Song getSongById(Long id) {
        return songRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Song not found with id " + id));
    }

    
    @Override
    public List<Song> getSongsByArtistId(Long artistId) {
        User artist = userRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found"));
        return songRepository.findByArtist(artist);
    }

    
    @Override
    @Transactional
    public Song saveSong(SongDto songDto, User artist, MultipartFile audioFile, MultipartFile coverFile) {
        log.info("Starting song upload: {} for artist ID: {}", songDto.getTitle(), artist.getId());
        if (audioFile == null || audioFile.isEmpty()) {
            log.error("Upload failed: Audio file is missing for song {}", songDto.getTitle());
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
                log.debug("Attached cover art for song: {}", songDto.getTitle());
            }

            Song saved = songRepository.save(song);
            log.info("Song successfully uploaded and saved with ID: {}", saved.getId());
            return saved;
        } catch (java.io.IOException e) {
            log.error("IO Exception during song upload: ", e);
            throw new RuntimeException("Failed to store media in database", e);
        }
    }

    
    @Override
    @Transactional
    public Song updateSong(Long id, SongDto songDto, Long artistId, MultipartFile coverFile) {
        log.info("Updating song ID: {} by artist ID: {}", id, artistId);
        Song song = getSongById(id);

        if (!song.getArtist().getId().equals(artistId)) {
            log.warn("Unauthorized modification attempt for song ID: {} by user ID: {}", id, artistId);
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
                log.debug("Updated cover art for song ID: {}", id);
            } catch (java.io.IOException e) {
                log.error("IO Exception during song cover update for song ID {}: ", id, e);
                throw new RuntimeException("Failed to update cover art in database", e);
            }
        }

        return songRepository.save(song);
    }

    
    @Override
    @Transactional
    public Song saveSong(Song song) {
        return songRepository.save(song);
    }

    
    @Override
    @Transactional
    public void deleteSong(Long id, Long artistId) {
        log.info("Attempting to delete song ID: {} by artist ID: {}", id, artistId);
        Song song = getSongById(id);

        if (!song.getArtist().getId().equals(artistId)) {
            log.warn("Unauthorized delete attempt for song ID: {} by user ID: {}", id, artistId);
            throw new RuntimeException("Unauthorized to delete this asset.");
        }

        
        
        historyRepository.deleteBySong(song);
        log.debug("Cleared history records for song ID: {}", id);

        
        entityManager.createNativeQuery("DELETE FROM user_liked_songs WHERE song_id = :songId")
                .setParameter("songId", id).executeUpdate();
        log.debug("Cleared liked_songs relationship for song ID: {}", id);

        
        entityManager.createNativeQuery("DELETE FROM playlist_songs WHERE song_id = :songId")
                .setParameter("songId", id).executeUpdate();
        log.debug("Cleared playlist relationships for song ID: {}", id);

        entityManager.flush();
        songRepository.delete(song);
        log.info("Song ID: {} successfully deleted from system.", id);
    }

    
    @Override
    @Transactional
    public void recordPlay(Long id, String username) {
        Song song = songRepository.findById(id).orElse(null);
        if (song != null) {
            song.setPlayCount(song.getPlayCount() + 1);
            songRepository.save(song);
            log.debug("Play count +1 for song ID: {} (Total: {})", id, song.getPlayCount());

            if (username != null && !username.isEmpty()) {
                User user = userRepository.findByUsername(username).orElse(null);
                if (user != null) {
                    com.revature.revplay.entity.History history = com.revature.revplay.entity.History.builder()
                            .user(user)
                            .song(song)
                            .build();
                    historyRepository.save(history);
                    log.debug("Listening history recorded for user: {} on song ID: {}", username, id);
                }
            }
        } else {
            log.warn("Attempted to record play for non-existent song ID: {}", id);
        }
    }
}
