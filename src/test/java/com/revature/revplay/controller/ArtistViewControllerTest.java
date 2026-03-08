package com.revature.revplay.controller;

import com.revature.revplay.entity.*;
import com.revature.revplay.exception.ResourceNotFoundException;
import com.revature.revplay.repository.*;
import com.revature.revplay.service.SocialService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ArtistViewControllerTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ArtistProfileRepository artistProfileRepository;

    @Mock
    private SongRepository songRepository;

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private SocialService socialService;

    @Mock
    private Authentication authentication;

    @Mock
    private Model model;

    @InjectMocks
    private ArtistViewController controller;

    @Test
    void shouldReturnArtistProfileView() {

        User artist = new User();
        artist.setId(1L);
        artist.setUsername("artist1");
        artist.setRole(Role.ARTIST);

        ArtistProfile profile = new ArtistProfile();

        when(userRepository.findByUsername("artist1"))
                .thenReturn(Optional.of(artist));

        when(artistProfileRepository.findById(1L))
                .thenReturn(Optional.of(profile));

        when(songRepository.findByArtist(artist))
                .thenReturn(List.of(new Song()));

        when(albumRepository.findByArtist(artist))
                .thenReturn(List.of(new Album()));

        when(socialService.getFollowerCount(1L))
                .thenReturn(10L);

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("user1");

        when(socialService.isFollowing(1L, "user1"))
                .thenReturn(true);

        String view = controller.viewArtistPublicProfile("artist1", authentication, model);

        assertEquals("artist/public-profile", view);

        verify(model).addAttribute("artist", artist);
        verify(model).addAttribute("profile", profile);
    }

    @Test
    void shouldThrowExceptionWhenArtistNotFound() {

        when(userRepository.findByUsername("artist1"))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                controller.viewArtistPublicProfile("artist1", null, model));
    }

    @Test
    void shouldThrowExceptionWhenArtistProfileNotFound() {

        User artist = new User();
        artist.setId(1L);
        artist.setRole(Role.ARTIST);

        when(userRepository.findByUsername("artist1"))
                .thenReturn(Optional.of(artist));

        when(artistProfileRepository.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () ->
                controller.viewArtistPublicProfile("artist1", null, model));
    }
}