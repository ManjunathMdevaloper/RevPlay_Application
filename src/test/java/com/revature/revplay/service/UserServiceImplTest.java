package com.revature.revplay.service;

import com.revature.revplay.dto.UserDto;
import com.revature.revplay.entity.ArtistProfile;
import com.revature.revplay.entity.Role;
import com.revature.revplay.entity.User;
import com.revature.revplay.repository.ArtistProfileRepository;
import com.revature.revplay.repository.PlaylistRepository;
import com.revature.revplay.repository.UserRepository;
import com.revature.revplay.service.impl.UserServiceImpl;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ArtistProfileRepository artistProfileRepository;

    @Mock
    private PlaylistRepository playlistRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void shouldReturnUserWhenUsernameExists() {

        User user = new User();
        user.setUsername("pooja");

        when(userRepository.findByUsername("pooja"))
                .thenReturn(Optional.of(user));

        User result = userService.getUserByUsername("pooja");

        assertNotNull(result);
        assertEquals("pooja", result.getUsername());
    }

    @Test
    void shouldReturnUserWhenIdentifierIsUsername() {
        User user = new User();
        user.setUsername("pooja");

        when(userRepository.findByUsernameOrEmail("pooja", "pooja"))
                .thenReturn(Optional.of(user));

        User result = userService.getUserByUsernameOrEmail("pooja");

        assertNotNull(result);
        assertEquals("pooja", result.getUsername());
    }

    @Test
    void shouldReturnUserWhenIdentifierIsEmail() {
        User user = new User();
        user.setEmail("pooja@gmail.com");

        when(userRepository.findByUsernameOrEmail("pooja@gmail.com", "pooja@gmail.com"))
                .thenReturn(Optional.of(user));

        User result = userService.getUserByUsernameOrEmail("pooja@gmail.com");

        assertNotNull(result);
        assertEquals("pooja@gmail.com", result.getEmail());
    }

    @Test
    void shouldVerifyUserWithValidUsernameAndEmail() {

        when(userRepository.findByUsernameAndEmail("pooja", "pooja@gmail.com"))
                .thenReturn(Optional.of(new User()));

        boolean result = userService.verifyUser("pooja", "pooja@gmail.com");

        assertTrue(result);
    }

    @Test
    void shouldUpdatePasswordSuccessfully() {

        User user = new User();
        user.setUsername("pooja");

        when(userRepository.findByUsername("pooja"))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.encode("newpass"))
                .thenReturn("encodedPass");

        userService.updatePassword("pooja", "newpass");

        verify(userRepository, times(1)).save(user);
        assertEquals("encodedPass", user.getPassword());
    }

    @Test
    void shouldReturnUserProfileForRegularUser() {

        User user = new User();
        user.setId(1L);
        user.setUsername("pooja");
        user.setEmail("pooja@gmail.com");
        user.setDisplayName("Pooja");
        user.setRole(Role.USER);

        user.setLikedSongs(new HashSet<>());
        user.setFollowing(new HashSet<>());

        when(userRepository.findByUsername("pooja"))
                .thenReturn(Optional.of(user));

        when(playlistRepository.findByUser_Username("pooja"))
                .thenReturn(new ArrayList<>());

        UserDto result = userService.getUserProfile("pooja");

        assertNotNull(result);
        assertEquals("pooja", result.getUsername());
        assertEquals("pooja@gmail.com", result.getEmail());
    }

    @Test
    void shouldReturnArtistProfileWhenUserIsArtist() {

        User user = new User();
        user.setId(1L);
        user.setUsername("artist1");
        user.setEmail("artist@gmail.com");
        user.setDisplayName("Artist One");
        user.setRole(Role.ARTIST);

        user.setLikedSongs(new HashSet<>());
        user.setFollowing(new HashSet<>());

        ArtistProfile profile = new ArtistProfile();
        profile.setArtistName("Artist One");
        profile.setGenre("Rock");
        profile.setInstagramUrl("insta.com/artist");

        when(userRepository.findByUsername("artist1"))
                .thenReturn(Optional.of(user));

        when(artistProfileRepository.findById(1L))
                .thenReturn(Optional.of(profile));

        when(playlistRepository.findByUser_Username("artist1"))
                .thenReturn(new ArrayList<>());

        UserDto result = userService.getUserProfile("artist1");

        assertNotNull(result);
        assertEquals("artist1", result.getUsername());
        assertEquals("Artist One", result.getArtistName());
        assertEquals("Rock", result.getGenre());
    }

    @Test
    void shouldUpdateUserProfileForNormalUser() {

        User user = new User();
        user.setUsername("pooja");
        user.setRole(Role.USER);

        UserDto dto = new UserDto();
        dto.setDisplayName("Pooja");
        dto.setBio("Music lover");

        when(userRepository.findByUsername("pooja"))
                .thenReturn(Optional.of(user));

        userService.updateUserProfile("pooja", dto, null, null);

        verify(userRepository).save(user);
    }

    @Test
    void shouldUpdateArtistProfile() {

        User user = new User();
        user.setUsername("artist1");
        user.setRole(Role.ARTIST);

        ArtistProfile profile = new ArtistProfile();
        profile.setUser(user);

        UserDto dto = new UserDto();
        dto.setArtistName("ArtistName");

        when(userRepository.findByUsername("artist1"))
                .thenReturn(Optional.of(user));

        when(artistProfileRepository.findById(user.getId()))
                .thenReturn(Optional.of(profile));

        userService.updateUserProfile("artist1", dto, null, null);

        verify(artistProfileRepository).save(profile);
    }

    @Test
    void shouldUpdateProfilePicture() throws Exception {

        User user = new User();
        user.setUsername("pooja");
        user.setRole(Role.USER);

        UserDto dto = new UserDto();

        MultipartFile file = mock(MultipartFile.class);

        when(file.isEmpty()).thenReturn(false);
        when(file.getBytes()).thenReturn("data".getBytes());
        when(file.getContentType()).thenReturn("image/png");

        when(userRepository.findByUsername("pooja"))
                .thenReturn(Optional.of(user));

        userService.updateUserProfile("pooja", dto, file, null);

        verify(userRepository).save(user);
    }
}
