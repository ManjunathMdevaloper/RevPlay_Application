package com.revature.revplay.controller;

import com.revature.revplay.entity.Playlist;
import com.revature.revplay.entity.Song;
import com.revature.revplay.repository.AlbumRepository;
import com.revature.revplay.repository.UserRepository;
import com.revature.revplay.service.PlaylistService;
import com.revature.revplay.service.SearchService;
import com.revature.revplay.service.SongService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DiscoveryControllerTest {

    @Mock
    private SongService songService;

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private SearchService searchService;

    @Mock
    private PlaylistService playlistService;

    @Mock
    private Model model;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private DiscoveryController controller;

    @Test
    void shouldLoadHomePage() {

        when(songService.getAllSongs()).thenReturn(List.of(new Song()));
        when(albumRepository.findAll()).thenReturn(List.of());
        when(userRepository.findAllArtists()).thenReturn(List.of());
        when(searchService.getAllGenres()).thenReturn(List.of("Rock"));
        when(playlistService.getAllPublicPlaylists()).thenReturn(List.of());

        String view = controller.home(model);

        assertEquals("discovery/list", view);
        verify(model).addAttribute(eq("songs"), any());
    }

    @Test
    void shouldLoadDiscoveryWithoutQuery() {

        Playlist playlist = new Playlist();
        playlist.setName("Hits");

        when(playlistService.getAllPublicPlaylists())
                .thenReturn(List.of(playlist));

        String view = controller.discovery(null, model);

        assertEquals("discovery/explore-playlists", view);

        verify(model).addAttribute("publicPlaylists", List.of(playlist));
    }

    @Test
    void shouldFilterPlaylistsByQuery() {

        Playlist playlist = new Playlist();
        playlist.setName("Rock Hits");

        when(playlistService.getAllPublicPlaylists())
                .thenReturn(List.of(playlist));

        String view = controller.discovery("rock", model);

        assertEquals("discovery/explore-playlists", view);

        verify(model).addAttribute(eq("query"), eq("rock"));
    }

    @Test
    void shouldViewSongDetails() {

        Song song = new Song();
        song.setId(1L);

        when(songService.getSongById(1L)).thenReturn(song);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("user");

        when(playlistService.getUserPlaylists("user")).thenReturn(List.of());

        String view = controller.viewSongDetails(1L, model, authentication);

        assertEquals("discovery/detail", view);

        verify(model).addAttribute("song", song);
    }
}