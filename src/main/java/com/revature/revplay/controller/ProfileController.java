package com.revature.revplay.controller;

import com.revature.revplay.dto.UserDto;
import com.revature.revplay.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


@Controller
@RequestMapping("/profile")
@Log4j2
public class ProfileController {

    private final UserService userService;

    
    public ProfileController(UserService userService) {
        this.userService = userService;
    }

    
    @GetMapping
    public String viewProfile(Model model, Authentication authentication) {
        log.info("User {} is viewing their profile", authentication.getName());
        String username = authentication.getName();
        UserDto userDto = userService.getUserProfile(username);
        model.addAttribute("userProfile", userDto);
        return "profile/view";
    }

    
    @GetMapping("/edit")
    public String editProfileForm(Model model, Authentication authentication) {
        log.info("User {} is accessing the profile edit form", authentication.getName());
        String username = authentication.getName();
        UserDto userDto = userService.getUserProfile(username);
        model.addAttribute("userProfile", userDto);
        return "profile/edit";
    }

    
    @GetMapping("/api/me")
    @ResponseBody
    public UserDto getCurrentUser(Authentication authentication) {
        log.debug("Request to fetch current user profile via API");
        if (authentication == null || !authentication.isAuthenticated())
            return null;
        return userService.getUserProfile(authentication.getName());
    }

    
    @PostMapping("/edit")
    public String updateProfile(
            @ModelAttribute("userProfile") UserDto userDto,
            @RequestParam(value = "profilePic", required = false) MultipartFile profilePic,
            @RequestParam(value = "bannerPic", required = false) MultipartFile bannerPic,
            Authentication authentication) {

        String username = authentication.getName();
        log.info("User {} is updating their profile details", username);

        if (profilePic != null && !profilePic.isEmpty()) {
            log.debug("User {} uploaded a new profile picture ({} bytes)", username, profilePic.getSize());
        }
        if (bannerPic != null && !bannerPic.isEmpty()) {
            log.debug("User {} uploaded a new banner image ({} bytes)", username, bannerPic.getSize());
        }

        userService.updateUserProfile(username, userDto, profilePic, bannerPic);
        log.info("User {}'s profile successfully updated", username);

        return "redirect:/profile?success";
    }
}
