package com.revature.revplay.service;

import com.revature.revplay.service.impl.CustomUserDetailsService;
import com.revature.revplay.entity.Role;
import com.revature.revplay.entity.User;
import com.revature.revplay.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void shouldLoadUserByUsernameSuccessfully() {

        User user = new User();
        user.setUsername("pooja");
        user.setPassword("password");
        user.setRole(Role.USER);

        when(userRepository.findByUsernameOrEmail("pooja", "pooja"))
                .thenReturn(Optional.of(user));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("pooja");

        assertEquals("pooja", userDetails.getUsername());
        assertEquals("password", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities()
                .stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_USER")));
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {

        when(userRepository.findByUsernameOrEmail("unknown", "unknown"))
                .thenReturn(Optional.empty());

        assertThrows(
                UsernameNotFoundException.class,
                () -> customUserDetailsService.loadUserByUsername("unknown")
        );
    }
}
