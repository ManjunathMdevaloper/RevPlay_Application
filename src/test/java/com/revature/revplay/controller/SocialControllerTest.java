package com.revature.revplay.controller;

import com.revature.revplay.entity.History;
import com.revature.revplay.entity.User;
import com.revature.revplay.repository.HistoryRepository;
import com.revature.revplay.repository.UserRepository;
import com.revature.revplay.service.SocialService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SocialControllerTest {

    @Mock
    private SocialService socialService;

    @Mock
    private HistoryRepository historyRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private Authentication authentication;

    @Mock
    private Model model;

    @InjectMocks
    private SocialController controller;

    @Test
    void shouldToggleFollowSuccessfully() {

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("user");
        when(socialService.toggleFollowArtist(1L, "user")).thenReturn(true);

        ResponseEntity<Boolean> response = controller.toggleFollow(1L, authentication);

        assertEquals(true, response.getBody());
    }

    @Test
    void shouldReturnUnauthorizedWhenToggleFollowWithoutAuth() {

        ResponseEntity<Boolean> response = controller.toggleFollow(1L, null);

        assertEquals(401, response.getStatusCodeValue());
    }

    @Test
    void shouldReturnFollowStatus() {

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("user");
        when(socialService.isFollowing(1L, "user")).thenReturn(true);

        ResponseEntity<Boolean> response = controller.getFollowStatus(1L, authentication);

        assertEquals(true, response.getBody());
    }

    @Test
    void shouldLoadTrendingPage() {

        when(socialService.getTopTrendingSongs(20)).thenReturn(List.of());
        when(socialService.getTopArtists(10)).thenReturn(List.of());

        String view = controller.viewTrending(model);

        assertEquals("discovery/trending", view);
        verify(model).addAttribute(eq("topSongs"), any());
        verify(model).addAttribute(eq("topArtists"), any());
    }

    @Test
    void shouldLoadHistoryPage() {

        User user = new User();
        user.setUsername("user");

        History history = new History();

        when(authentication.getName()).thenReturn("user");
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));
        when(historyRepository.findByUserOrderByPlayedAtDesc(user)).thenReturn(List.of(history));

        String view = controller.viewHistory(authentication, model);

        assertEquals("discovery/history", view);
        verify(model).addAttribute(eq("recentHistory"), any());
        verify(model).addAttribute(eq("completeHistory"), any());
    }

    @Test
    void shouldClearHistory() {

        User user = new User();
        user.setUsername("user");

        when(authentication.getName()).thenReturn("user");
        when(userRepository.findByUsername("user")).thenReturn(Optional.of(user));

        String view = controller.clearHistory(authentication);

        assertEquals("redirect:/social/history", view);

        verify(historyRepository).deleteByUser(user);
    }
}
