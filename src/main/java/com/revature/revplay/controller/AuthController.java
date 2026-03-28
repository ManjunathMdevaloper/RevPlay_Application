package com.revature.revplay.controller;

import com.revature.revplay.dto.UserRegistrationDto;
import com.revature.revplay.entity.User;
import com.revature.revplay.service.AuthService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;


@Controller
@Log4j2
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/login")
    public String loginForm() {
        log.info("Accessing login page");
        return "auth/login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        log.info("Accessing registration page");
        model.addAttribute("user", new UserRegistrationDto());
        return "auth/register";
    }

    @PostMapping("/register/save")
    public String register(@Valid @ModelAttribute("user") UserRegistrationDto userDto,
            BindingResult result,
            Model model) {
        log.info("Attempting to register user: {} with email: {}", userDto.getUsername(), userDto.getEmail());
        User existingUserEmail = authService.findByEmail(userDto.getEmail());
        if (existingUserEmail != null && existingUserEmail.getEmail() != null
                && !existingUserEmail.getEmail().isEmpty()) {
            log.warn("Registration failed: Email {} already exists", userDto.getEmail());
            result.rejectValue("email", null, "There is already an account registered with the same email");
        }

        User existingUserUsername = authService.findByUsername(userDto.getUsername());
        if (existingUserUsername != null && existingUserUsername.getUsername() != null
                && !existingUserUsername.getUsername().isEmpty()) {
            log.warn("Registration failed: Username {} already exists", userDto.getUsername());
            result.rejectValue("username", null, "There is already an account registered with the same username");
        }

        if (result.hasErrors()) {
            log.warn("Registration failed for user {} due to validation errors", userDto.getUsername());
            model.addAttribute("user", userDto);
            return "auth/register";
        }

        authService.registerUser(userDto);
        log.info("User {} successfully registered", userDto.getUsername());
        return "redirect:/login?registerSuccess";
    }
}
