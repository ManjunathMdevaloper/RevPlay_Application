package com.revature.revplay.controller;

import com.revature.revplay.dto.SongDto;
import com.revature.revplay.entity.Album;
import com.revature.revplay.entity.Song;
import com.revature.revplay.entity.User;
import com.revature.revplay.repository.AlbumRepository;
import com.revature.revplay.repository.HistoryRepository;
import com.revature.revplay.service.SocialService;
import com.revature.revplay.service.SongService;
import com.revature.revplay.service.UserService;
import jakarta.persistence.Query;

import jakarta.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtistDashboardControllerTest {

    @Mock
    private SongService songService;

    @Mock
    private UserService userService;

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private SocialService socialService;

    @Mock
    private EntityManager entityManager;

    @Mock
    private HistoryRepository historyRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private ArtistDashboardController controller;

    @Test
    void shouldRenderDashboard() {

        User artist = new User();
        artist.setId(1L);

        Song song = new Song();
        song.setPlayCount(10L);

        when(authentication.getName()).thenReturn("artist");
        when(userService.getUserByUsername("artist")).thenReturn(artist);
        when(songService.getSongsByArtistId(1L)).thenReturn(List.of(song));
        when(albumRepository.findByArtist(artist)).thenReturn(List.of(new Album()));
        when(socialService.getTotalArtistStreams(1L)).thenReturn(100L);
        when(socialService.getFollowerCount(1L)).thenReturn(5L);
        when(socialService.getFollowers(1L)).thenReturn(List.of());

        String view = controller.renderDashboard(authentication, model);

        assertEquals("artist/dashboard", view);
        verify(model).addAttribute(eq("songs"), any());
    }

    @Test
    void shouldRenderCreateSongForm() {

        User artist = new User();
        artist.setId(1L);

        when(authentication.getName()).thenReturn("artist");
        when(userService.getUserByUsername("artist")).thenReturn(artist);

        String view = controller.renderCreateSongForm(authentication, model);

        assertEquals("artist/song-form", view);
    }

    @Test
    void shouldCreateSong() {

        User artist = new User();
        artist.setId(1L);

        when(authentication.getName()).thenReturn("artist");
        when(userService.getUserByUsername("artist")).thenReturn(artist);

        String view = controller.createSong(
                authentication,
                new SongDto(),
                null,
                null,
                redirectAttributes
        );

        assertEquals("redirect:/artist/dashboard", view);
    }

    @Test
    void shouldDeleteSong() {

        User artist = new User();
        artist.setId(1L);

        when(authentication.getName()).thenReturn("artist");
        when(userService.getUserByUsername("artist")).thenReturn(artist);

        String view = controller.deleteSong(1L, authentication, redirectAttributes);

        assertEquals("redirect:/artist/dashboard", view);

        verify(songService).deleteSong(1L, 1L);
    }

    @Test
    void shouldRenderCreateAlbumForm() {

        String view = controller.renderCreateAlbumForm(model);

        assertEquals("artist/album-form", view);
    }

    @Test
    void shouldRenderEditSongForm() {

        User artist = new User();
        artist.setId(1L);

        Song song = new Song();
        song.setId(10L);
        song.setArtist(artist);
        song.setTitle("Test Song");

        when(authentication.getName()).thenReturn("artist");
        when(userService.getUserByUsername("artist")).thenReturn(artist);
        when(songService.getSongById(10L)).thenReturn(song);
        when(albumRepository.findByArtist(artist)).thenReturn(List.of(new Album()));

        String view = controller.renderEditSongForm(10L, authentication, model);

        assertEquals("artist/song-form", view);
    }

    @Test
    void shouldEditSong() {

        User artist = new User();
        artist.setId(1L);

        when(authentication.getName()).thenReturn("artist");
        when(userService.getUserByUsername("artist")).thenReturn(artist);

        SongDto songDto = new SongDto();

        String view = controller.editSong(
                10L,
                authentication,
                songDto,
                null,
                redirectAttributes
        );

        assertEquals("redirect:/artist/dashboard", view);

        verify(songService).updateSong(10L, songDto, 1L, null);
    }

    @Test
    void shouldCreateAlbum() {

        User artist = new User();
        artist.setId(1L);

        Album album = new Album();
        album.setName("Test Album");

        when(authentication.getName()).thenReturn("artist");
        when(userService.getUserByUsername("artist")).thenReturn(artist);
        when(albumRepository.save(any(Album.class))).thenReturn(album);

        String view = controller.createAlbum(
                authentication,
                new com.revature.revplay.dto.AlbumDto(),
                null,
                redirectAttributes
        );

        assertEquals("redirect:/artist/dashboard", view);

        verify(albumRepository).save(any(Album.class));
    }

    @Test
    void shouldRenderEditAlbumForm() {

        User artist = new User();
        artist.setId(1L);

        Album album = new Album();
        album.setId(5L);
        album.setArtist(artist);

        when(authentication.getName()).thenReturn("artist");
        when(userService.getUserByUsername("artist")).thenReturn(artist);
        when(albumRepository.findById(5L)).thenReturn(java.util.Optional.of(album));

        String view = controller.renderEditAlbumForm(5L, authentication, model);

        assertEquals("artist/album-form", view);
    }

    @Test
    void shouldEditAlbum() {

        User artist = new User();
        artist.setId(1L);

        Album album = new Album();
        album.setId(5L);
        album.setArtist(artist);

        when(authentication.getName()).thenReturn("artist");
        when(userService.getUserByUsername("artist")).thenReturn(artist);
        when(albumRepository.findById(5L)).thenReturn(java.util.Optional.of(album));

        String view = controller.editAlbum(
                5L,
                authentication,
                new com.revature.revplay.dto.AlbumDto(),
                null,
                redirectAttributes
        );

        assertEquals("redirect:/artist/dashboard", view);

        verify(albumRepository).save(album);
    }

    @Test
    void shouldDeleteAlbum() {

        User artist = new User();
        artist.setId(1L);

        Album album = new Album();
        album.setId(5L);
        album.setArtist(artist);

        Song song = new Song();
        song.setId(10L);
        song.setAlbumId(5L);

        Query query = mock(Query.class);

        when(entityManager.createNativeQuery(anyString())).thenReturn(query);
        when(query.setParameter(anyString(), any())).thenReturn(query);
        when(query.executeUpdate()).thenReturn(1);

        when(authentication.getName()).thenReturn("artist");
        when(userService.getUserByUsername("artist")).thenReturn(artist);
        when(albumRepository.findById(5L)).thenReturn(java.util.Optional.of(album));
        when(songService.getSongsByArtistId(1L)).thenReturn(List.of(song));

        String view = controller.deleteAlbum(5L, authentication, redirectAttributes);

        assertEquals("redirect:/artist/dashboard", view);
    }
}