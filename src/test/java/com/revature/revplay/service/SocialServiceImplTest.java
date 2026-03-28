package com.revature.revplay.service;

import com.revature.revplay.service.impl.SocialServiceImpl;
import com.revature.revplay.entity.Song;
import com.revature.revplay.entity.User;
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
class SocialServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private SongRepository songRepository;

    @InjectMocks
    private SocialServiceImpl socialService;

    @Test
    void shouldFollowArtistWhenNotFollowing() {

        User user = new User();
        user.setId(1L);
        user.setFollowing(new HashSet<>());

        User artist = new User();
        artist.setId(2L);

        when(userRepository.findByUsername("pooja")).thenReturn(Optional.of(user));
        when(userRepository.findById(2L)).thenReturn(Optional.of(artist));
        when(userRepository.countFollowersForUser("pooja", 2L)).thenReturn(0L);

        boolean result = socialService.toggleFollowArtist(2L, "pooja");

        assertTrue(result);
        verify(userRepository).save(user);
    }

    @Test
    void shouldUnfollowArtistWhenAlreadyFollowing() {

        User user = new User();
        user.setId(1L);
        user.setFollowing(new HashSet<>());

        User artist = new User();
        artist.setId(2L);

        user.getFollowing().add(artist);

        when(userRepository.findByUsername("pooja")).thenReturn(Optional.of(user));
        when(userRepository.findById(2L)).thenReturn(Optional.of(artist));
        when(userRepository.countFollowersForUser("pooja", 2L)).thenReturn(1L);

        boolean result = socialService.toggleFollowArtist(2L, "pooja");

        assertFalse(result);
        verify(userRepository).save(user);
    }

    @Test
    void shouldReturnFalseWhenUsernameIsNull() {

        boolean result = socialService.isFollowing(1L, null);

        assertFalse(result);
    }

    @Test
    void shouldReturnFollowerCount() {

        when(userRepository.countFollowersByArtistId(2L)).thenReturn(10L);

        long count = socialService.getFollowerCount(2L);

        assertEquals(10L, count);
    }

    @Test
    void shouldReturnFollowersList() {

        List<User> followers = List.of(new User(), new User());

        when(userRepository.findFollowersByArtistId(1L)).thenReturn(followers);

        List<User> result = socialService.getFollowers(1L);

        assertEquals(2, result.size());
    }

    @Test
    void shouldReturnTopTrendingSongs() {

        List<Song> songs = List.of(new Song(), new Song());

        when(songRepository.findTopTrendingSongs(any())).thenReturn(songs);

        List<Song> result = socialService.getTopTrendingSongs(5);

        assertEquals(2, result.size());
    }

    @Test
    void shouldReturnTotalArtistStreams() {

        when(songRepository.getTotalPlayCountByArtistId(1L)).thenReturn(100L);

        long streams = socialService.getTotalArtistStreams(1L);

        assertEquals(100L, streams);
    }

}
