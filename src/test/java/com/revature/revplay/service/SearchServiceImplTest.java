package com.revature.revplay.service;

import com.revature.revplay.service.impl.SearchServiceImpl;
import com.revature.revplay.dto.SearchResultDto;
import com.revature.revplay.entity.Role;
import com.revature.revplay.entity.Song;
import com.revature.revplay.entity.User;
import com.revature.revplay.repository.AlbumRepository;
import com.revature.revplay.repository.PlaylistRepository;
import com.revature.revplay.repository.SongRepository;
import com.revature.revplay.repository.UserRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchServiceImplTest {

    @Mock
    private SongRepository songRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private PlaylistRepository playlistRepository;

    @InjectMocks
    private SearchServiceImpl searchService;

    @Test
    void shouldSearchAllCategories() {

        String keyword = "rock";

        List<Song> songs = List.of(new Song());
        List<User> artists = List.of(new User());

        when(songRepository.findByTitleContainingIgnoreCase(keyword)).thenReturn(songs);
        when(userRepository.findByDisplayNameContainingIgnoreCaseAndRole(keyword, Role.ARTIST)).thenReturn(artists);
        when(albumRepository.findByNameContainingIgnoreCaseAndSongsIsNotEmpty(keyword)).thenReturn(List.of());
        when(playlistRepository.findByNameContainingIgnoreCaseAndIsPublicTrue(keyword)).thenReturn(List.of());

        SearchResultDto result = searchService.searchAll(keyword);

        assertNotNull(result);
        assertEquals(1, result.getSongs().size());
        assertEquals(1, result.getArtists().size());
    }

    @Test
    void shouldReturnEmptyResultsWhenKeywordIsBlank() {

        SearchResultDto result = searchService.searchAll(" ");

        assertNotNull(result);
        assertNull(result.getSongs());
    }

    @Test
    void shouldFilterSongsWithArtist() {

        User artist = new User();
        artist.setId(1L);

        when(userRepository.findById(1L)).thenReturn(Optional.of(artist));
        when(songRepository.searchAndFilterSongs(any(), any(), eq(artist), any(), any()))
                .thenReturn(List.of(new Song()));

        List<Song> songs = searchService.filterSongs("song", "pop", 1L, null, null);

        assertEquals(1, songs.size());
    }

    @Test
    void shouldFilterSongsWithoutArtist() {

        when(songRepository.searchAndFilterSongs(any(), any(), isNull(), any(), any()))
                .thenReturn(List.of(new Song()));

        List<Song> songs = searchService.filterSongs("song", "pop", null, null, null);

        assertEquals(1, songs.size());
    }

    @Test
    void shouldReturnSortedGenres() {

        List<String> genres = Arrays.asList("rock", "pop", "rock", "", null);

        when(songRepository.findAllGenres()).thenReturn(genres);

        List<String> result = searchService.getAllGenres();

        assertEquals(2, result.size());
        assertEquals("pop", result.get(0));
        assertEquals("rock", result.get(1));
    }
}
