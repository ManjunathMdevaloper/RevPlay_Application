package com.revature.revplay.service;

import com.revature.revplay.dto.SongDto;
import com.revature.revplay.entity.Song;
import com.revature.revplay.entity.User;
import com.revature.revplay.repository.HistoryRepository;
import com.revature.revplay.repository.SongRepository;
import com.revature.revplay.repository.UserRepository;
import com.revature.revplay.service.impl.SongServiceImpl;

import jakarta.persistence.Query;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import jakarta.persistence.EntityManager;

import java.util.List;
import java.util.Optional;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SongServiceImplTest {

    @Mock
    private SongRepository songRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private HistoryRepository historyRepository;

    @Mock
    private EntityManager entityManager;

    @InjectMocks
    private SongServiceImpl songService;

    @Test
    void shouldReturnAllSongs() {

        List<Song> songs = new ArrayList<>();
        songs.add(new Song());
        songs.add(new Song());

        when(songRepository.findAll()).thenReturn(songs);

        List<Song> result = songService.getAllSongs();

        assertEquals(2, result.size());
    }

    @Test
    void shouldReturnSongWhenIdExists() {

        Song song = new Song();
        song.setId(1L);

        when(songRepository.findById(1L)).thenReturn(Optional.of(song));

        Song result = songService.getSongById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void shouldReturnSongsForArtist() {

        User artist = new User();
        artist.setId(1L);

        List<Song> songs = new ArrayList<>();
        songs.add(new Song());

        when(userRepository.findById(1L)).thenReturn(Optional.of(artist));
        when(songRepository.findByArtist(artist)).thenReturn(songs);

        List<Song> result = songService.getSongsByArtistId(1L);

        assertEquals(1, result.size());
    }

    @Test
    void shouldIncreasePlayCountAndSaveHistory() {

        Song song = new Song();
        song.setId(1L);
        song.setPlayCount(5L);

        User user = new User();
        user.setUsername("pooja");

        when(songRepository.findById(1L)).thenReturn(Optional.of(song));
        when(userRepository.findByUsername("pooja")).thenReturn(Optional.of(user));

        songService.recordPlay(1L, "pooja");

        assertEquals(6, song.getPlayCount());

        verify(songRepository).save(song);
        verify(historyRepository).save(any());
    }

    @Test
    void shouldUpdateSongWhenArtistIsOwner() {

        Song song = new Song();
        song.setId(1L);

        User artist = new User();
        artist.setId(10L);

        song.setArtist(artist);

        SongDto dto = new SongDto();
        dto.setTitle("Updated Song");
        dto.setGenre("Pop");

        when(songRepository.findById(1L)).thenReturn(Optional.of(song));
        when(songRepository.save(any(Song.class))).thenReturn(song);

        Song result = songService.updateSong(1L, dto, 10L, null);

        assertEquals("Updated Song", result.getTitle());
        assertEquals("Pop", result.getGenre());

        verify(songRepository).save(song);
    }

    @Test
    void shouldThrowExceptionWhenArtistNotOwner() {

        Song song = new Song();
        song.setId(1L);

        User artist = new User();
        artist.setId(10L);

        song.setArtist(artist);

        SongDto dto = new SongDto();

        when(songRepository.findById(1L)).thenReturn(Optional.of(song));

        assertThrows(RuntimeException.class, () ->
                songService.updateSong(1L, dto, 20L, null));
    }

    @Test
    void shouldDeleteSongWhenArtistIsOwner() {

        Song song = new Song();
        song.setId(1L);

        User artist = new User();
        artist.setId(10L);

        song.setArtist(artist);

        when(songRepository.findById(1L))
                .thenReturn(Optional.of(song));

        // Mock entity manager query
        Query mockQuery = mock(Query.class);

        when(entityManager.createNativeQuery(anyString())).thenReturn(mockQuery);
        when(mockQuery.setParameter(anyString(), any())).thenReturn(mockQuery);
        when(mockQuery.executeUpdate()).thenReturn(1);

        songService.deleteSong(1L, 10L);

        verify(historyRepository).deleteBySong(song);
        verify(songRepository).delete(song);
    }
}