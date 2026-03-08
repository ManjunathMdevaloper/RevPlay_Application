package com.revature.revplay.controller;

import com.revature.revplay.dto.UserRegistrationDto;
import com.revature.revplay.entity.User;
import com.revature.revplay.service.AuthService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @Mock
    private Model model;

    @Mock
    private BindingResult bindingResult;

    @InjectMocks
    private AuthController controller;

    @Test
    void shouldReturnLoginPage() {

        String view = controller.loginForm();

        assertEquals("auth/login", view);
    }

    @Test
    void shouldShowRegistrationForm() {

        String view = controller.showRegistrationForm(model);

        assertEquals("auth/register", view);
        verify(model).addAttribute(eq("user"), any(UserRegistrationDto.class));
    }

    @Test
    void shouldReturnRegisterPageWhenEmailExists() {

        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setEmail("test@mail.com");
        dto.setUsername("testuser");

        User existing = new User();
        existing.setEmail("test@mail.com");

        when(authService.findByEmail("test@mail.com")).thenReturn(existing);
        when(authService.findByUsername("testuser")).thenReturn(null);
        when(bindingResult.hasErrors()).thenReturn(true);

        String view = controller.register(dto, bindingResult, model);

        assertEquals("auth/register", view);
    }

    @Test
    void shouldRegisterUserSuccessfully() {

        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setEmail("test@mail.com");
        dto.setUsername("testuser");

        when(authService.findByEmail(any())).thenReturn(null);
        when(authService.findByUsername(any())).thenReturn(null);
        when(bindingResult.hasErrors()).thenReturn(false);

        String view = controller.register(dto, bindingResult, model);

        assertEquals("redirect:/login?registerSuccess", view);

        verify(authService).registerUser(dto);
    }
}