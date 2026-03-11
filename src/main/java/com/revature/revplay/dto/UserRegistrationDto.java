package com.revature.revplay.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

/**
 * This Data Transfer Object (DTO) represents the structured information
 * required for a new user to join RevPlay.
 * It serves as a temporary container to safely move registration data from the
 * web form into the backend services.
 * By using validation annotations, it ensures that only high-quality, verified
 * data (like valid emails) enters our database.
 * This class acts as the initial handshake between a new user and the
 * application's security and account management systems.
 * It provides a clean, decoupled way to handle authentication details
 * separately from the main User entity.
 */
@Data
public class UserRegistrationDto {
    /**
     * The unique identity name chosen by the user for logging in and social
     * display.
     * This field is mandatory and must be unique across the entire platform.
     * It is used during the registration handshake to check for existing accounts.
     */
    @NotEmpty(message = "Username should not be empty")
    private String username;

    /**
     * The primary contact and recovery email address for the user account.
     * It is validated using standard internet email patterns to ensure it's
     * reachable.
     * This is essential for secure communication and account verification
     * processes.
     */
    @NotEmpty(message = "Email should not be empty")
    @Email
    private String email;

    /**
     * The confidential secret used to secure the user's account for processing.
     * Note: This plain-text value is only held temporarily in this DTO and is
     * immediately hashed (encrypted) before ever being saved to a database.
     */
    @NotEmpty(message = "Password should not be empty")
    private String password;

    /**
     * A flag indicating if the new user intends to upload music as a professional
     * creator.
     * If true, the system will automatically provision artist-specific tools and
     * dashboards.
     * This choice defines the user's primary "Role" and permissions within the
     * RevPlay ecosystem.
     */
    private boolean isArtist;

    @NotEmpty(message = "Security question should not be empty")
    private String securityQuestion;

    @NotEmpty(message = "Security answer should not be empty")
    private String securityAnswer;

    private String securityHint;
}