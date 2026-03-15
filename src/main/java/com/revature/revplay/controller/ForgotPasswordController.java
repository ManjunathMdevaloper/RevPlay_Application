package com.revature.revplay.controller;

import com.revature.revplay.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * This controller manages the "Forgot Password" and account recovery workflows.
 * It provides a secure path for users to regain access to their accounts if
 * they lose their
 * login credentials. The process involves multiple steps: user verification,
 * identity
 * confirmation, and finally, the secure update of the account password.
 * By integrating with the UserService, it ensures that recovery attempts are
 * validated
 * against existing database records to prevent unauthorized account takeovers.
 */
@Controller
@Log4j2
public class ForgotPasswordController {

    private final UserService userService;

    /**
     * Standard constructor for injecting the UserService dependency.
     * 
     * This setup allows the controller to:
     * 1. Access the master 'User' database for identity verification.
     * 2. Perform secure, hashed password updates using industry-standard encoders.
     * 3. Coordinate between the recovery forms and the underlying business logic.
     * 4. This follows the Dependency Injection pattern to keep the routing and
     * logic separated.
     */
    public ForgotPasswordController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Renders the initial account recovery request form.
     * 
     * The recovery entry point process involves:
     * 1. Displaying a simple form where users can provide their username and email.
     * 2. Returning the "auth/forgot-password" view to begin the identification
     * sequence.
     * 3. This page acts as the first safety check in the password reset pipeline.
     */
    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "auth/forgot-password";
    }

    /**
     * Processes the user's identification request and verifies their account data.
     * 
     * The verification logic handles:
     * 1. Taking the provided username and email from the form and checking the user
     * table.
     * 2. Using the UserService to perform a strict match check for identity
     * security.
     * 3. if valid, transitioning the user to the "Reset Password" stage with their
     * username preserved.
     * 4. If invalid, returning an error message to let the user know no matching
     * account exists.
     * 5. This method is the critical "Gatekeeper" that must be passed before a
     * password can be changed.
     */
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

            // Compare answers ignoring case
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

    /**
     * Handles the final step of updating the user's secret password.
     * 
     * This secure update method manages:
     * 1. Capturing the new password and a confirmation copy from the form.
     * 2. Verifying that both entries match exactly to prevent accidental typos.
     * 3. triggering the UserService's update logic to hash and store the new
     * secret.
     * 4. Redirecting the user back to the login page with a "resetSuccess" flag for
     * feedback.
     * 5. This finalizes the recovery cycle and restores the user's access to the
     * platform.
     */
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