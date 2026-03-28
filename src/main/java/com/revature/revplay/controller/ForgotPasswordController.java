package com.revature.revplay.controller;

import com.revature.revplay.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@Log4j2
public class ForgotPasswordController {

    private final UserService userService;

    public ForgotPasswordController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("username") String usernameOrEmail,
            Model model) {
        log.info("Processing forgot password request for user: {}", usernameOrEmail);
        try {
            com.revature.revplay.entity.User user = userService.getUserByUsernameOrEmail(usernameOrEmail);
            log.info("User found, proceeding to security question step for identifier: {}", usernameOrEmail);
            model.addAttribute("username", user.getUsername());
            model.addAttribute("securityQuestion", user.getSecurityQuestion());
            model.addAttribute("securityHint", user.getSecurityHint());
            return "auth/forgot-password-question";
        } catch (RuntimeException e) {
            log.warn("Forgot password verification failed: User not found: {}", usernameOrEmail);
            model.addAttribute("error", "No account found with that username or email.");
            return "auth/forgot-password";
        }
    }

    @PostMapping("/verify-security-question")
    public String verifySecurityQuestion(@RequestParam("username") String username,
            @RequestParam("securityAnswer") String securityAnswer,
            Model model) {
        log.info("Processing security question answer for user: {}", username);
        try {
            com.revature.revplay.entity.User user = userService.getUserByUsername(username);

            if (user.getSecurityAnswer() != null &&
                    user.getSecurityAnswer().equalsIgnoreCase(securityAnswer.trim())) {
                log.info("Security question verification successful for user: {}", username);
                model.addAttribute("username", username);
                return "auth/reset-password";
            } else {
                log.warn("Security question verification failed for user: {}", username);
                model.addAttribute("username", username);
                model.addAttribute("securityQuestion", user.getSecurityQuestion());
                model.addAttribute("securityHint", user.getSecurityHint());
                model.addAttribute("error", "Incorrect answer to the security question.");
                return "auth/forgot-password-question";
            }
        } catch (RuntimeException e) {
            log.warn("User not found during security question verification: {}", username);
            return "redirect:/forgot-password";
        }
    }

    @PostMapping("/reset-password")
    public String updatePassword(@RequestParam("username") String username,
            @RequestParam("password") String password,
            @RequestParam("confirmPassword") String confirmPassword,
            Model model) {
        log.info("Processing password reset for user: {}", username);
        if (!password.equals(confirmPassword)) {
            log.warn("Password reset failed: Passwords do not match for user: {}", username);
            model.addAttribute("username", username);
            model.addAttribute("error", "Passwords do not match.");
            return "auth/reset-password";
        }

        userService.updatePassword(username, password);
        log.info("Password successfully reset for user: {}", username);
        return "redirect:/login?resetSuccess";
    }
}