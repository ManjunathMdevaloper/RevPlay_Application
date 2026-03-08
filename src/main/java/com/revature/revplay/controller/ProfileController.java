package com.revature.revplay.controller;

import com.revature.revplay.dto.UserDto;
import com.revature.revplay.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * This controller manages all activities related to user profiles within the
 * system.
 * It provides functionality for users to view their personal details, edit
 * their
 * information, and upload custom images like profile pictures and banners.
 * By using the @RequestMapping("/profile") annotation at the class level, all
 * methods
 * in this file share a common URL prefix, keeping the code organized and
 * intuitive.
 * It interacts heavily with the security context to ensure users can only
 * modify
 * their own data and not someone else's.
 */
@Controller
@RequestMapping("/profile")
public class ProfileController {

    private final UserService userService;

    /**
     * Standard constructor for injecting the UserService dependency.
     * 
     * The importance of this injection includes:
     * 1. Allowing the controller to delegate complex user-related logic to the
     * service layer.
     * 2. Maintaining a clean separation of concerns between web handling and
     * business logic.
     * 3. Facilitating the retrieval of deep user data like bio, email, and
     * following counts.
     * 4. Ensuring that file upload handling for profiles is processed in a
     * centralized way.
     * 5. Supporting the modular architecture that makes the RevPlay application
     * stable and scalable.
     */
    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Handles requests to view the authenticated user's profile dashboard.
     * 
     * The execution flow for this method is as follows:
     * 1. It extracts the username of the currently logged-in user from the
     * Authentication object.
     * 2. It queries the UserService to fetch a complete Data Transfer Object (DTO)
     * for that profile.
     * 3. It attaches the populated UserDto to the 'Model' for the front-end to
     * display.
     * 4. It returns the "profile/view" logical view name to render the dashboard
     * UI.
     * 5. This method is the primary destination for users checking their music
     * stats and profile look.
     */
    @GetMapping
    public String viewProfile(Model model, Authentication authentication) {
        String username = authentication.getName();
        UserDto userDto = userService.getUserProfile(username);
        model.addAttribute("userProfile", userDto);
        return "profile/view";
    }

    /**
     * Displays the settings page where a user can modify their profile details.
     * 
     * The logic for preparing the edit form includes:
     * 1. Identifying the active user via Spring Security's authentication context.
     * 2. Retrieving existing profile data so input fields can be pre-filled for the
     * user.
     * 3. Passing the data to the "profile/edit" template where users can type
     * changes.
     * 4. This method avoids making the user start from a blank form every time they
     * want to edit.
     * 5. It provides a seamless transition from viewing a profile to refining its
     * details.
     */
    @GetMapping("/edit")
    public String editProfileForm(Model model, Authentication authentication) {
        String username = authentication.getName();
        UserDto userDto = userService.getUserProfile(username);
        model.addAttribute("userProfile", userDto);
        return "profile/edit";
    }

    /**
     * A utility API endpoint that returns the current user's profile data in JSON
     * format.
     * 
     * This method is useful for front-end scripts and features because:
     * 1. It uses @ResponseBody to return raw data instead of an HTML page.
     * 2. It allows JavaScript components to dynamically fetch the logged-in user's
     * info.
     * 3. It performs a safety check to return null if no user is currently
     * authenticated.
     * 4. It enables real-time UI updates without requiring a full page refresh.
     * 5. It acts as a lightweight helper for the various interactive features of
     * the music player.
     */
    @GetMapping("/api/me")
    @ResponseBody
    public UserDto getCurrentUser(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated())
            return null;
        return userService.getUserProfile(authentication.getName());
    }

    /**
     * Processes form submissions when a user saves changes to their profile.
     * 
     * The complex operations handled here include:
     * 1. Capturing text updates like BIO or display names from the UserDto model.
     * 2. Safely accepting multi-part file uploads for both Profile Pictures and
     * Banner Images.
     * 3. Passing all modified data and files to the UserService for database and
     * disk storage.
     * 4. Enforcing security by ensuring the 'update' happens only for the
     * authenticated username.
     * 5. Redirecting the user back to their profile with a success notification
     * flag.
     * 6. This method ensures that all visual and textual changes are synchronized
     * across the app.
     */
    @PostMapping("/edit")
    public String updateProfile(
            @ModelAttribute("userProfile") UserDto userDto,
            @RequestParam(value = "profilePic", required = false) MultipartFile profilePic,
            @RequestParam(value = "bannerPic", required = false) MultipartFile bannerPic,
            Authentication authentication) {

        String username = authentication.getName();
        userService.updateUserProfile(username, userDto, profilePic, bannerPic);

        return "redirect:/profile?success";
    }
}