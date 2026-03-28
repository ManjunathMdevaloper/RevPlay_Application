package com.revature.revplay.service;

import com.revature.revplay.dto.PlaylistDto;
import com.revature.revplay.entity.Playlist;
import com.revature.revplay.entity.Song;
import com.revature.revplay.entity.User;
import com.revature.revplay.repository.PlaylistRepository;
import com.revature.revplay.repository.SongRepository;
import com.revature.revplay.repository.UserRepository;
import com.revature.revplay.service.impl.PlaylistServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PlaylistServiceImplTest {

    @Mock
    private PlaylistRepository playlistRepository;

    @Mock
    private SongRepository songRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private PlaylistServiceImpl playlistService;

    @Test
    void shouldCreatePlaylistForUser() {

        User user = new User();
        user.setUsername("pooja");

        PlaylistDto dto = new PlaylistDto();
        dto.setName("My Playlist");
        dto.setDescription("My fav songs");
        dto.setPublic(true);

        when(userRepository.findByUsername("pooja"))
                .thenReturn(Optional.of(user));

        when(playlistRepository.save(any(Playlist.class)))
                .thenReturn(new Playlist());

        Playlist result = playlistService.createPlaylist(dto, "pooja");

        assertNotNull(result);

        verify(playlistRepository).save(any(Playlist.class));
    }

    @Test
    void shouldReturnPlaylistById() {

        Playlist playlist = new Playlist();
        playlist.setId(1L);
        playlist.setSongs(new HashSet<>());

        when(playlistRepository.findById(1L))
                .thenReturn(Optional.of(playlist));

        Playlist result = playlistService.getPlaylistById(1L);

        assertEquals(1L, result.getId());
    }

    @Test
    void shouldUpdatePlaylistWhenOwnerMatches() {

        User user = new User();
        user.setUsername("pooja");

        Playlist playlist = new Playlist();
        playlist.setUser(user);

        PlaylistDto dto = new PlaylistDto();
        dto.setName("Updated Playlist");
        dto.setDescription("Updated desc");
        dto.setPublic(false);

        when(playlistRepository.findById(1L))
                .thenReturn(Optional.of(playlist));

        when(playlistRepository.save(any()))
                .thenReturn(playlist);

        Playlist result = playlistService.updatePlaylist(1L, dto, "pooja");

        assertEquals("Updated Playlist", result.getName());
    }

    @Test
    void shouldAddSongToPlaylist() {

        User user = new User();
        user.setUsername("pooja");

        Playlist playlist = new Playlist();
        playlist.setUser(user);
        playlist.setSongs(new HashSet<>());

        Song song = new Song();
        song.setId(10L);

        when(playlistRepository.findById(1L))
                .thenReturn(Optional.of(playlist));

        when(songRepository.findById(10L))
                .thenReturn(Optional.of(song));

        when(playlistRepository.save(any()))
                .thenReturn(playlist);

        Playlist result = playlistService.addSongToPlaylist(1L, 10L, "pooja");

        assertTrue(result.getSongs().contains(song));
    }

    @Test
    void shouldToggleLikeSong() {

        User user = new User();
        user.setUsername("pooja");
        user.setLikedSongs(new HashSet<>());

        Song song = new Song();
        song.setId(5L);

        when(userRepository.findByUsername("pooja"))
                .thenReturn(Optional.of(user));

        when(songRepository.findById(5L))
                .thenReturn(Optional.of(song));

        boolean result = playlistService.toggleLikeSong(5L, "pooja");

        assertTrue(result);
        assertTrue(user.getLikedSongs().contains(song));
    }

    @Test
    void shouldReturnUserPlaylists() {

        List<Playlist> playlists = List.of(new Playlist(), new Playlist());

        when(playlistRepository.findByUser_Username("pooja"))
                .thenReturn(playlists);

        List<Playlist> result = playlistService.getUserPlaylists("pooja");

        assertEquals(2, result.size());
    }

    @Test
    void shouldReturnAllPublicPlaylists() {

        Playlist playlist = new Playlist();
        playlist.setSongs(new HashSet<>());

        when(playlistRepository.findByIsPublicTrue())
                .thenReturn(List.of(playlist));

        List<Playlist> result = playlistService.getAllPublicPlaylists();

        assertEquals(1, result.size());
    }

    @Test
    void shouldDeletePlaylistWhenOwnerMatches() {

        User user = new User();
        user.setUsername("pooja");

        Playlist playlist = new Playlist();
        playlist.setUser(user);

        when(playlistRepository.findById(1L))
                .thenReturn(Optional.of(playlist));

        playlistService.deletePlaylist(1L, "pooja");

        verify(playlistRepository).delete(playlist);
    }

    @Test
    void shouldRemoveSongFromPlaylist() {

        User user = new User();
        user.setUsername("pooja");

        Song song = new Song();
        song.setId(10L);

        Playlist playlist = new Playlist();
        playlist.setUser(user);
        playlist.setSongs(new HashSet<>(Set.of(song)));

        when(playlistRepository.findById(1L))
                .thenReturn(Optional.of(playlist));

        when(songRepository.findById(10L))
                .thenReturn(Optional.of(song));

        when(playlistRepository.save(any()))
                .thenReturn(playlist);

        Playlist result = playlistService.removeSongFromPlaylist(1L, 10L, "pooja");

        assertFalse(result.getSongs().contains(song));
    }

    @Test
    void shouldReturnLikedSongs() {

        Song song = new Song();
        song.setId(1L);

        User user = new User();
        user.setUsername("pooja");
        user.setLikedSongs(new HashSet<>(Set.of(song)));

        when(userRepository.findByUsername("pooja"))
                .thenReturn(Optional.of(user));

        Set<Song> result = playlistService.getLikedSongs("pooja");

        assertEquals(1, result.size());
    }

    @Test
    void shouldReturnTrueWhenSongIsLiked() {

        Song song = new Song();
        song.setId(1L);

        User user = new User();
        user.setUsername("pooja");
        user.setLikedSongs(new HashSet<>(Set.of(song)));

        when(userRepository.findByUsername("pooja"))
                .thenReturn(Optional.of(user));

        when(songRepository.findById(1L))
                .thenReturn(Optional.of(song));

        boolean result = playlistService.isSongLiked(1L, "pooja");

        assertTrue(result);
    }

}
