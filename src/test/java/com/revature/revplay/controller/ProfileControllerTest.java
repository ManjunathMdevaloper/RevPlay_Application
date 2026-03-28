package com.revature.revplay.controller;

import com.revature.revplay.dto.UserDto;
import com.revature.revplay.service.UserService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfileControllerTest {

    @Mock
    private UserService userService;

    @Mock
    private Authentication authentication;

    @Mock
    private Model model;

    @Mock
    private MultipartFile profilePic;

    @Mock
    private MultipartFile bannerPic;

    @InjectMocks
    private ProfileController controller;

    @Test
    void shouldViewProfile() {

        UserDto dto = new UserDto();

        when(authentication.getName()).thenReturn("user");
        when(userService.getUserProfile("user")).thenReturn(dto);

        String view = controller.viewProfile(model, authentication);

        assertEquals("profile/view", view);

        verify(model).addAttribute("userProfile", dto);
    }

    @Test
    void shouldLoadEditProfileForm() {

        UserDto dto = new UserDto();

        when(authentication.getName()).thenReturn("user");
        when(userService.getUserProfile("user")).thenReturn(dto);

        String view = controller.editProfileForm(model, authentication);

        assertEquals("profile/edit", view);

        verify(model).addAttribute("userProfile", dto);
    }

    @Test
    void shouldReturnCurrentUser() {

        UserDto dto = new UserDto();

        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getName()).thenReturn("user");

        when(userService.getUserProfile("user")).thenReturn(dto);

        UserDto result = controller.getCurrentUser(authentication);

        assertEquals(dto, result);
    }

    @Test
    void shouldUpdateProfile() {

        UserDto dto = new UserDto();

        when(authentication.getName()).thenReturn("user");

        String view = controller.updateProfile(dto, profilePic, bannerPic, authentication);

        assertEquals("redirect:/profile?success", view);

        verify(userService).updateUserProfile("user", dto, profilePic, bannerPic);
    }
}
