package com.revature.revplay.controller;

import com.revature.revplay.dto.PlaylistDto;
import com.revature.revplay.entity.Playlist;
import com.revature.revplay.entity.Song;
import com.revature.revplay.service.PlaylistService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LibraryControllerTest {

    @Mock
    private PlaylistService playlistService;

    @Mock
    private Authentication authentication;

    @Mock
    private Model model;

    @Mock
    private RedirectAttributes redirectAttributes;

    @InjectMocks
    private LibraryController controller;

    @Test
    void shouldViewLikedSongs() {

        Song song = new Song();

        when(authentication.getName()).thenReturn("user");
        when(playlistService.getLikedSongs("user"))
                .thenReturn(Set.of(song));

        String view = controller.viewLikedSongs(authentication, model);

        assertEquals("library/liked", view);
        verify(model).addAttribute("songs", Set.of(song));
    }

    @Test
    void shouldReturnLikeStatusFalseWhenNotAuthenticated() {

        ResponseEntity<Boolean> response =
                controller.getLikeStatus(1L);

        assertEquals(false, response.getBody());
    }

    @Test
    void shouldViewPlaylists() {

        when(authentication.getName()).thenReturn("user");
        when(playlistService.getUserPlaylists("user"))
                .thenReturn(List.of(new Playlist()));

        String view = controller.viewPlaylists(authentication, model);

        assertEquals("library/playlists", view);
        verify(model).addAttribute(eq("playlists"), any());
    }

    @Test
    void shouldCreatePlaylist() {

        PlaylistDto dto = new PlaylistDto();

        when(authentication.getName()).thenReturn("user");

        String view = controller.createPlaylist(dto, authentication, redirectAttributes);

        assertEquals("redirect:/library/playlists", view);

        verify(playlistService).createPlaylist(dto, "user");
    }

    @Test
    void shouldViewPlaylistDetails() {

        Playlist playlist = new Playlist();

        when(playlistService.getPlaylistById(1L))
                .thenReturn(playlist);

        String view = controller.viewPlaylistDetails(1L, model);

        assertEquals("library/playlist-detail", view);

        verify(model).addAttribute("playlist", playlist);
    }

    @Test
    void shouldDeletePlaylist() {

        when(authentication.getName()).thenReturn("user");

        String view = controller.deletePlaylist(1L, authentication, redirectAttributes);

        assertEquals("redirect:/library/playlists", view);

        verify(playlistService).deletePlaylist(1L, "user");
    }

    @Test
    void shouldRemoveSongFromPlaylist() {

        when(authentication.getName()).thenReturn("user");

        String view = controller.removeSongFromPlaylistNative(
                1L,
                2L,
                authentication,
                redirectAttributes
        );

        assertEquals("redirect:/library/playlists/1", view);

        verify(playlistService).removeSongFromPlaylist(1L, 2L, "user");
    }

    @Test
    void shouldReturnEmptyLikedSongs() {

        when(authentication.getName()).thenReturn("user");
        when(playlistService.getLikedSongs("user"))
                .thenReturn(Set.of());

        String view = controller.viewLikedSongs(authentication, model);

        assertEquals("library/liked", view);

        verify(model).addAttribute("songs", Set.of());
    }


    @Test
    void shouldViewEmptyPlaylists() {

        when(authentication.getName()).thenReturn("user");
        when(playlistService.getUserPlaylists("user"))
                .thenReturn(List.of());

        String view = controller.viewPlaylists(authentication, model);

        assertEquals("library/playlists", view);

        verify(model).addAttribute(eq("playlists"), any());
    }

    @Test
    void shouldCreatePlaylistAndAddSuccessMessage() {

        PlaylistDto dto = new PlaylistDto();

        when(authentication.getName()).thenReturn("user");

        String view = controller.createPlaylist(dto, authentication, redirectAttributes);

        assertEquals("redirect:/library/playlists", view);

        verify(playlistService).createPlaylist(dto, "user");

        verify(redirectAttributes)
                .addFlashAttribute(anyString(), anyString());
    }

    @Test
    void shouldDeletePlaylistAndAddMessage() {

        when(authentication.getName()).thenReturn("user");

        String view = controller.deletePlaylist(1L, authentication, redirectAttributes);

        assertEquals("redirect:/library/playlists", view);

        verify(playlistService).deletePlaylist(1L, "user");

        verify(redirectAttributes)
                .addFlashAttribute(anyString(), anyString());
    }

    @Test
    void shouldRemoveSongAndAddFlashMessage() {

        when(authentication.getName()).thenReturn("user");

        String view = controller.removeSongFromPlaylistNative(
                1L,
                2L,
                authentication,
                redirectAttributes
        );

        assertEquals("redirect:/library/playlists/1", view);

        verify(playlistService)
                .removeSongFromPlaylist(1L, 2L, "user");

        verify(redirectAttributes)
                .addFlashAttribute(anyString(), anyString());
    }

    @Test
    void shouldViewPlaylistDetailsWithSongs() {

        Playlist playlist = new Playlist();

        Set<Song> songs = new java.util.HashSet<>();
        songs.add(new Song());
        songs.add(new Song());

        playlist.setSongs(songs);

        when(playlistService.getPlaylistById(1L))
                .thenReturn(playlist);

        String view = controller.viewPlaylistDetails(1L, model);

        assertEquals("library/playlist-detail", view);

        verify(model).addAttribute("playlist", playlist);
    }
}
