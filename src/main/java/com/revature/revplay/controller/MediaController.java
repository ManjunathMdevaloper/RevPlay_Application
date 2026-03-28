package com.revature.revplay.controller;

import com.revature.revplay.entity.Album;
import com.revature.revplay.entity.ArtistProfile;
import com.revature.revplay.entity.Song;
import com.revature.revplay.entity.User;
import com.revature.revplay.repository.AlbumRepository;
import com.revature.revplay.repository.ArtistProfileRepository;
import com.revature.revplay.repository.SongRepository;
import com.revature.revplay.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;


@Controller
@RequestMapping("/api/media")
@Log4j2
public class MediaController {

    private final SongRepository songRepository;
    private final AlbumRepository albumRepository;
    private final UserRepository userRepository;
    private final ArtistProfileRepository artistProfileRepository;

    
    public MediaController(SongRepository songRepository, AlbumRepository albumRepository,
            UserRepository userRepository, ArtistProfileRepository artistProfileRepository) {
        this.songRepository = songRepository;
        this.albumRepository = albumRepository;
        this.userRepository = userRepository;
        this.artistProfileRepository = artistProfileRepository;
    }

    
    @GetMapping("/song/{id}/audio")
    public ResponseEntity<Resource> getSongAudio(@PathVariable("id") Long id) {
        log.debug("Request for song audio ID: {}", id);
        Song song = songRepository.findById(id).orElse(null);
        if (song == null || song.getAudioData() == null) {
            log.warn("Song audio not found for ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE,
                        song.getAudioContentType() != null ? song.getAudioContentType() : "audio/mpeg")
                .body(new ByteArrayResource(song.getAudioData()));
    }

    
    @GetMapping("/song/{id}/cover")
    public ResponseEntity<byte[]> getSongCover(@PathVariable("id") Long id) {
        log.debug("Request for song cover ID: {}", id);
        Song song = songRepository.findById(id).orElse(null);
        if (song == null || song.getCoverArtData() == null) {
            log.debug("Song cover NOT found for ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE,
                        song.getCoverArtContentType() != null ? song.getCoverArtContentType() : "image/jpeg")
                .body(song.getCoverArtData());
    }

    
    @GetMapping("/album/{id}/cover")
    public ResponseEntity<byte[]> getAlbumCover(@PathVariable("id") Long id) {
        log.debug("Request for album cover ID: {}", id);
        Album album = albumRepository.findById(id).orElse(null);
        if (album == null || album.getCoverArtData() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE,
                        album.getCoverArtContentType() != null ? album.getCoverArtContentType() : "image/jpeg")
                .body(album.getCoverArtData());
    }

    
    @GetMapping("/user/{id}/picture")
    public ResponseEntity<byte[]> getUserPicture(@PathVariable("id") Long id) {
        log.debug("Request for user profile picture ID: {}", id);
        User user = userRepository.findById(id).orElse(null);
        if (user == null || user.getProfilePictureData() == null) {
            log.debug("User picture NOT found for ID: {}", id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE,
                        user.getProfilePictureContentType() != null ? user.getProfilePictureContentType()
                                : "image/jpeg")
                .body(user.getProfilePictureData());
    }

    
    @GetMapping("/artist/{id}/banner")
    public ResponseEntity<byte[]> getArtistBanner(@PathVariable("id") Long id) {
        log.debug("Request for artist banner ID: {}", id);
        ArtistProfile profile = artistProfileRepository.findById(id).orElse(null);
        if (profile == null || profile.getBannerImageData() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE,
                        profile.getBannerImageContentType() != null ? profile.getBannerImageContentType()
                                : "image/jpeg")
                .body(profile.getBannerImageData());
    }
}