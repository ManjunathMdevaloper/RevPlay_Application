package com.revature.revplay.controller;

import com.revature.revplay.dto.UserRegistrationDto;
import com.revature.revplay.entity.User;
import com.revature.revplay.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * This controller handles all authentication-related web requests for the
 * application.
 * It manages the user journey for signing up and logging into the platform.
 * By using the @Controller annotation, it communicates with the Thymeleaf
 * template engine
 * to render HTML pages like 'login' and 'register'.
 * It acts as the bridge between the user's input on the web forms and our
 * internal
 * security and database systems.
 */

@Controller
public class AuthController {

    private final AuthService authService;

    /**
     * Standard constructor used to inject the AuthService dependency.
     * 
     * The injection mechanism ensures that:
     * 1. This controller has access to the core business logic for user management.
     * 2. The code remains testable by allowing mock services to be swapped in.
     * 3. It follows the Dependency Injection architectural pattern favored by
     * Spring.
     * 4. The authService handles the actual database operations and security
     * hashing.
     * 5. This keeps the controller focused strictly on handling web requests.
     */
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Displays the customized login page to the user.
     * 
     * The logic for this simple endpoint involves:
     * 1. Mapping a HTTP GET request on the "/login" path to this specific method.
     * 2. Returning the logical view name "auth/login", which points to the HTML
     * file.
     * 3. Spring Security intercepts this route to handle any logout messages or
     * error flags.
     * 4. It serves as the entrance for returning users who wish to access their
     * private library.
     * 5. The page itself is styled with CSS to match the premium aesthetics of
     * RevPlay.
     */
    @GetMapping("/login")
    public String loginForm() {
        return "auth/login";
    }

    /**
     * Prepares and displays the registration form for new account creation.
     * 
     * The process for showing the registration page includes:
     * 1. Creating a fresh 'UserRegistrationDto' object to act as a form-backing
     * bean.
     * 2. Attaching this empty object to the 'Model' so Thymeleaf can bind input
     * fields to it.
     * 3. Returning the "auth/register" template to the browser.
     * 4. This method ensures that the form doesn't crash when it tries to access
     * empty fields.
     * 5. It sets the stage for capturing username, email, and password from the
     * prospective user.
     * 6. It also handles the 'isArtist' flag for users who want to upload their own
     * music.
     */
    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new UserRegistrationDto());
        return "auth/register";
    }

    /**
     * Processes the submission of the registration form and creates a new user
     * account.
     * 
     * This complex method performs multiple critical validation and logic steps:
     * 1. It uses @Valid to automatically check if inputs meet basic rules (like
     * non-empty fields).
     * 2. It checks against the database to ensure the provided email address isn't
     * already in use.
     * 3. It performs a secondary check to verify the unique username availability.
     * 4. If any errors are found (like duplicates), it sends the user back to the
     * form with messages.
     * 5. If everything is valid, it calls the service layer to encrypt the password
     * and save the record.
     * 6. Finally, it redirects the user to the login page with a success flag to
     * welcome them.
     * 7. This prevents 'double submission' bugs and ensures a smooth onboarding
     * experience.
     */
    @PostMapping("/register/save")
    public String register(@Valid @ModelAttribute("user") UserRegistrationDto userDto,
            BindingResult result,
            Model model) {
        User existingUserEmail = authService.findByEmail(userDto.getEmail());
        if (existingUserEmail != null && existingUserEmail.getEmail() != null
                && !existingUserEmail.getEmail().isEmpty()) {
            result.rejectValue("email", null, "There is already an account registered with the same email");
        }

        User existingUserUsername = authService.findByUsername(userDto.getUsername());
        if (existingUserUsername != null && existingUserUsername.getUsername() != null
                && !existingUserUsername.getUsername().isEmpty()) {
            result.rejectValue("username", null, "There is already an account registered with the same username");
        }

        if (result.hasErrors()) {
            model.addAttribute("user", userDto);
            return "auth/register";
        }

        authService.registerUser(userDto);
        return "redirect:/login?registerSuccess";
    }
}

