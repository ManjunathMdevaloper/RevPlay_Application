package com.revature.revplay.service;

import com.revature.revplay.dto.UserDto;
import com.revature.revplay.entity.User;
import org.springframework.web.multipart.MultipartFile;

/**
 * This interface defines the contract for all user-related business operations
 * in RevPlay.
 * It serves as an abstraction layer between the web controllers and the
 * database logic.
 * By defining methods here, we ensure that different implementations (like a
 * standard DB
 * implementation or a Mock for testing) follow the same structural rules.
 * It covers essential features from profile management to account recovery and
 * security updates.
 */
public interface UserService {

    /**
     * Retrieves a full User entity based strictly on their unique username.
     * 
     * This method is critical for:
     * 1. Internal security lookups during the authentication process.
     * 2. Linking songs, playlists, and follows back to a real person in the system.
     * 3. Fetching raw database data that might not be suitable for the public DTOs.
     * 4. Powering various backend checks where only the username is initially
     * known.
     */
    User getUserByUsername(String username);

    /**
     * Fetches a lightweight UserDto optimized for display on profile pages.
     * 
     * The importance of this specific method includes:
     * 1. Protecting sensitive data like passwords by only returning display-safe
     * info.
     * 2. Including calculated fields like bio, image paths, and role-specific
     * flags.
     * 3. Providing the essential data needed to render the Thymeleaf profile
     * templates.
     * 4. Reducing the amount of data transferred between the server and the
     * browser.
     */
    UserDto getUserProfile(String username);

    /**
     * Orchestrates the update of a user's profile information and media assets.
     * 
     * This multi-purpose method manages:
     * 1. Updating text-based metadata like the user's bio or email address.
     * 2. Processing and storing binary file uploads for new profile photos.
     * 3. Handling the replacement of artist banner images for brand pages.
     * 4. Ensuring that file storage rules are followed when new images are
     * provided.
     * 5. Maintaining data integrity by linking the updates to the authenticated
     * user.
     */
    void updateUserProfile(String username, UserDto userDto, MultipartFile profilePic, MultipartFile bannerPic);

    /**
     * Verifies that a specific username matches a specific email address in the
     * system.
     * 
     * This validation check is primarily used for:
     * 1. The first step of the 'Forgot Password' recovery workflow.
     * 2. Preventing unauthorized users from triggering password resets for accounts
     * they don't own.
     * 3. Providing an extra layer of identity confirmation before sensitive
     * operations.
     */
    boolean verifyUser(String username, String email);

    /**
     * Safely updates a user's password with a new securely hashed value.
     * 
     * This security-focused method handles:
     * 1. The final stage of account recovery after successful email/username
     * verification.
     * 2. Ensuring the new password is properly encoded before it hits the database.
     * 3. Clearing out any temporary reset tokens or states once the update is
     * complete.
     */
    void updatePassword(String username, String newPassword);
}