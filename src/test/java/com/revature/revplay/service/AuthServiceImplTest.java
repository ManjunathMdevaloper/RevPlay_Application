package com.revature.revplay.service;

import com.revature.revplay.service.impl.AuthServiceImpl;
import com.revature.revplay.dto.UserRegistrationDto;
import com.revature.revplay.entity.ArtistProfile;
import com.revature.revplay.entity.Role;
import com.revature.revplay.entity.User;
import com.revature.revplay.repository.ArtistProfileRepository;
import com.revature.revplay.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private ArtistProfileRepository artistProfileRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthServiceImpl authService;

    private UserRegistrationDto createDto(boolean artist) {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setUsername("pooja");
        dto.setEmail("pooja@test.com");
        dto.setPassword("123456");
        dto.setArtist(artist);
        dto.setSecurityQuestion("Your pet name?");
        dto.setSecurityAnswer("myPet");
        dto.setSecurityHint("pet");

        return dto;
    }

    @Test
    void shouldThrowExceptionWhenUsernameExists() {

        UserRegistrationDto dto = createDto(false);

        when(userRepository.existsByUsername("pooja")).thenReturn(true);

        assertThrows(RuntimeException.class,
                () -> authService.registerUser(dto));
    }

    @Test
    void shouldThrowExceptionWhenEmailExists() {

        UserRegistrationDto dto = createDto(false);

        when(userRepository.existsByUsername("pooja")).thenReturn(false);
        when(userRepository.existsByEmail("pooja@test.com")).thenReturn(true);

        assertThrows(RuntimeException.class,
                () -> authService.registerUser(dto));
    }

    @Test
    void shouldRegisterNormalUserSuccessfully() {

        UserRegistrationDto dto = createDto(false);

        when(userRepository.existsByUsername("pooja")).thenReturn(false);
        when(userRepository.existsByEmail("pooja@test.com")).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("encoded123");

        User savedUser = new User();
        savedUser.setUsername("pooja");

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = authService.registerUser(dto);

        assertNotNull(result);
        assertEquals("pooja", result.getUsername());


        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());

        User capturedUser = userCaptor.getValue();

        assertEquals("pooja", capturedUser.getUsername());
        assertEquals("encoded123", capturedUser.getPassword());
        assertEquals("mypet", capturedUser.getSecurityAnswer()); 

        verify(artistProfileRepository, never()).save(any());
    }

    @Test
    void shouldRegisterArtistAndCreateProfile() {

        UserRegistrationDto dto = createDto(true);

        when(userRepository.existsByUsername("pooja")).thenReturn(false);
        when(userRepository.existsByEmail("pooja@test.com")).thenReturn(false);
        when(passwordEncoder.encode("123456")).thenReturn("encoded123");

        User savedUser = new User();
        savedUser.setUsername("pooja");
        savedUser.setRole(Role.ARTIST);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        User result = authService.registerUser(dto);

        assertNotNull(result);
        assertEquals(Role.ARTIST, result.getRole());

        verify(artistProfileRepository).save(any(ArtistProfile.class));
    }
    @Test
    void shouldFindUserByUsername() {

        User user = new User();
        user.setUsername("pooja");

        when(userRepository.findByUsername("pooja"))
                .thenReturn(Optional.of(user));

        User result = authService.findByUsername("pooja");

        assertEquals("pooja", result.getUsername());
    }

    @Test
    void shouldFindUserByEmail() {

        User user = new User();
        user.setEmail("pooja@test.com");

        when(userRepository.findByEmail("pooja@test.com"))
                .thenReturn(Optional.of(user));

        User result = authService.findByEmail("pooja@test.com");

        assertEquals("pooja@test.com", result.getEmail());
    }
}
