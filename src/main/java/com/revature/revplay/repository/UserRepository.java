package com.revature.revplay.repository;

import com.revature.revplay.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * This repository interface manages all database operations for the platform's
 * User accounts.
 * It provides the primary data access layer for authentication, identity
 * verification,
 * and social graph management (following/followers).
 * By extending JpaRepository, it inherits standard CRUD operations while
 * defining
 * specialized native and JPQL queries for complex social relationship
 * calculations.
 * This is the foundational component for any feature involving user identity or
 * community engagement.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
        /**
         * Finds a user record by their unique login username.
         * Used extensively during the login process and for fetching session context.
         */
        Optional<User> findByUsername(String username);

        /**
         * Performs a verification check for account recovery.
         * Match both username and email to confirm the user's identity before password
         * resetting.
         */
        Optional<User> findByUsernameAndEmail(String username, String email);

        /**
         * Calculates the total number of followers for a specific music creator.
         * This uses a high-performance native SQL count on the 'user_following_artists'
         * join table.
         * It drives the audience metrics seen on every artist's public profile page.
         */
        @org.springframework.data.jpa.repository.Query(value = "SELECT COUNT(*) FROM user_following_artists WHERE artist_id = :artistId", nativeQuery = true)
        long countFollowersByArtistId(@org.springframework.data.repository.query.Param("artistId") Long artistId);

        /**
         * Retrieves the full User entities of every person following a target artist.
         * This joined query maps the social relationship link back to individual
         * account data.
         * Used to build "Followers" lists and engagement dashboards for creators.
         */
        @org.springframework.data.jpa.repository.Query(value = "SELECT u.* FROM users u JOIN user_following_artists ufa ON u.id = ufa.user_id WHERE ufa.artist_id = :artistId", nativeQuery = true)
        List<User> findFollowersByArtistId(@org.springframework.data.repository.query.Param("artistId") Long artistId);

        /**
         * Filters the entire user table for accounts holding a specific authorization
         * Role.
         */
        @org.springframework.data.jpa.repository.Query("SELECT u FROM User u WHERE u.role = :role")
        List<User> findByRole(
                        @org.springframework.data.repository.query.Param("role") com.revature.revplay.entity.Role role);

        /**
         * Convenience method to quickly retrieve every professional 'ARTIST' in the
         * system.
         */
        default List<User> findAllArtists() {
                return findByRole(com.revature.revplay.entity.Role.ARTIST);
        }

        /**
         * Checks if a specific listener (by username) currently follows a target artist
         * ID.
         * This nested subquery handles resolving the username into an ID before
         * checking the relationship.
         * Essential for determining if the "Follow" button should be in a followed
         * state.
         */
        @org.springframework.data.jpa.repository.Query(value = "SELECT COUNT(*) FROM user_following_artists WHERE user_id = (SELECT id FROM users WHERE username = :username) AND artist_id = :artistId", nativeQuery = true)
        long countFollowersForUser(@org.springframework.data.repository.query.Param("username") String username,
                        @org.springframework.data.repository.query.Param("artistId") Long artistId);

        Optional<User> findByEmail(String email);

        Optional<User> findByUsernameOrEmail(String username, String email);

        /**
         * Security checks to ensure that new accounts do not collide with existing
         * usernames/emails.
         * Used in the registration workflow to provide instant feedback during sign-up.
         */
        boolean existsByUsername(String username);

        boolean existsByEmail(String email);

        /**
         * Performs a case-insensitive name search filtered by the user's role.
         * This is primarily used by the discovery engine to find professional artists
         * by their chosen names.
         */
        java.util.List<User> findByDisplayNameContainingIgnoreCaseAndRole(String displayName,
                        com.revature.revplay.entity.Role role);
}