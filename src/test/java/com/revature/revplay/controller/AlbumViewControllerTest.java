package com.revature.revplay.controller;

import com.revature.revplay.entity.Album;
import com.revature.revplay.entity.Song;
import com.revature.revplay.exception.ResourceNotFoundException;
import com.revature.revplay.repository.AlbumRepository;
import com.revature.revplay.repository.SongRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlbumViewControllerTest {

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private SongRepository songRepository;

    @Mock
    private Model model;

    @InjectMocks
    private AlbumViewController albumViewController;

    @Test
    void shouldReturnAlbumDetailViewWhenAlbumExists() {

        Long albumId = 1L;

        Album album = new Album();
        album.setId(albumId);

        List<Song> songs = List.of(new Song(), new Song());

        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        when(songRepository.findByAlbumId(albumId)).thenReturn(songs);

        String viewName = albumViewController.viewAlbumDetails(albumId, model);

        assertEquals("album/detail", viewName);

        verify(model).addAttribute("album", album);
        verify(model).addAttribute("songs", songs);
    }

    @Test
    void shouldThrowExceptionWhenAlbumNotFound() {

        Long albumId = 1L;

        when(albumRepository.findById(albumId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> albumViewController.viewAlbumDetails(albumId, model));
    }
}
