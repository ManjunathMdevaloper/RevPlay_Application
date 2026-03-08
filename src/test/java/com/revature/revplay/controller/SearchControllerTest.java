package com.revature.revplay.controller;

import com.revature.revplay.dto.SearchResultDto;
import com.revature.revplay.entity.Role;
import com.revature.revplay.entity.Song;
import com.revature.revplay.entity.User;
import com.revature.revplay.repository.AlbumRepository;
import com.revature.revplay.repository.UserRepository;
import com.revature.revplay.service.SearchService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.ui.Model;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchControllerTest {

    @Mock
    private SearchService searchService;

    @Mock
    private UserRepository userRepository;

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private Model model;

    @InjectMocks
    private SearchController controller;

    @Test
    void shouldReturnCategoriesWhenKeywordEmpty() {

        String view = controller.search("", model);

        assertEquals("search/categories", view);
    }

    @Test
    void shouldReturnSearchResultsWhenKeywordProvided() {

        SearchResultDto results = new SearchResultDto();

        when(searchService.searchAll("rock")).thenReturn(results);

        String view = controller.search("rock", model);

        assertEquals("search/results", view);

        verify(model).addAttribute("keyword", "rock");
        verify(model).addAttribute("results", results);
    }

    @Test
    void shouldLoadBrowseCategoriesPage() {

        User artist = new User();
        artist.setRole(Role.ARTIST);

        when(searchService.getAllGenres()).thenReturn(List.of("Rock", "Pop"));
        when(userRepository.findAll()).thenReturn(List.of(artist));
        when(albumRepository.findAll()).thenReturn(List.of());

        String view = controller.browseCategories(model);

        assertEquals("search/categories", view);

        verify(model).addAttribute(eq("genres"), any());
        verify(model).addAttribute(eq("artists"), any());
        verify(model).addAttribute(eq("albums"), any());
    }

    @Test
    void shouldFilterSongsSuccessfully() {

        List<Song> songs = List.of(new Song());

        when(searchService.filterSongs("song", "rock", 1L, 2L, 2024))
                .thenReturn(songs);

        when(searchService.getAllGenres()).thenReturn(List.of("Rock"));

        when(userRepository.findAll()).thenReturn(List.of());

        when(albumRepository.findAll()).thenReturn(List.of());

        String view = controller.filterSongs(
                "song",
                "rock",
                1L,
                2L,
                2024,
                model
        );

        assertEquals("search/filter", view);

        verify(model).addAttribute("songs", songs);
    }
}