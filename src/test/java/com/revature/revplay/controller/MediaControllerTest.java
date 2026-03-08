package com.revature.revplay.controller;

import com.revature.revplay.entity.*;
import com.revature.revplay.repository.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.ResponseEntity;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MediaControllerTest {

    @Mock
    private SongRepository songRepository;

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private ArtistProfileRepository artistProfileRepository;

    @InjectMocks
    private MediaController controller;

    @Test
    void shouldReturnSongAudio() {

        Song song = new Song();
        song.setAudioData("audio".getBytes());
        song.setAudioContentType("audio/mpeg");

        when(songRepository.findById(1L))
                .thenReturn(Optional.of(song));

        ResponseEntity<?> response = controller.getSongAudio(1L);

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void shouldReturnNotFoundWhenSongAudioMissing() {

        when(songRepository.findById(1L))
                .thenReturn(Optional.empty());

        ResponseEntity<?> response = controller.getSongAudio(1L);

        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    void shouldReturnSongCover() {

        Song song = new Song();
        song.setCoverArtData("img".getBytes());
        song.setCoverArtContentType("image/jpeg");

        when(songRepository.findById(1L))
                .thenReturn(Optional.of(song));

        ResponseEntity<?> response = controller.getSongCover(1L);

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void shouldReturnAlbumCover() {

        Album album = new Album();
        album.setCoverArtData("img".getBytes());
        album.setCoverArtContentType("image/jpeg");

        when(albumRepository.findById(1L))
                .thenReturn(Optional.of(album));

        ResponseEntity<?> response = controller.getAlbumCover(1L);

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void shouldReturnUserPicture() {

        User user = new User();
        user.setProfilePictureData("img".getBytes());
        user.setProfilePictureContentType("image/jpeg");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        ResponseEntity<?> response = controller.getUserPicture(1L);

        assertEquals(200, response.getStatusCodeValue());
    }

    @Test
    void shouldReturnArtistBanner() {

        ArtistProfile profile = new ArtistProfile();
        profile.setBannerImageData("img".getBytes());
        profile.setBannerImageContentType("image/jpeg");

        when(artistProfileRepository.findById(1L))
                .thenReturn(Optional.of(profile));

        ResponseEntity<?> response = controller.getArtistBanner(1L);

        assertEquals(200, response.getStatusCodeValue());
    }
}