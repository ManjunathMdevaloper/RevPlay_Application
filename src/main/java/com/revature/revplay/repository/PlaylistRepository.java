package com.revature.revplay.repository;

import com.revature.revplay.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This repository interface manages the database persistence for user-created
 * collections (Playlists).
 * It provides the primary data access layer for organizing songs into thematic
 * lists,
 * capturing both private personal collections and public community-shared
 * broadcasts.
 * By extending JpaRepository, it supports complex lookups such as
 * case-insensitive
 * global searches for public playlists and secure ownership-based retrievals
 * for the creator.
 * This is the core component for all user-driven content organization on the
 * platform.
 */
@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    /**
     * Performs a global search for community-shared playlists containing the target
     * query string.
     * This filter explicitly restricts results to 'Public' playlists to respect
     * user privacy.
     */
    List<Playlist> findByNameContainingIgnoreCaseAndIsPublicTrue(String name);

    /**
     * Retrieves all playlists owned by a specific User entity.
     * Used to populate the "My Library" and "Playlist Management" dashboards for
     * the current user.
     */
    List<Playlist> findByUser(com.revature.revplay.entity.User user);

    /**
     * Retrieves all playlists owned by a specific User identified by their
     * username.
     * Used for building public-facing profile pages where the User object is not
     * yet fully loaded.
     */
    List<Playlist> findByUser_Username(String username);

    /**
     * Fetches every public playlist currently available on the platform.
     * This powers the "Community Picks" or "Global Discovery" section of the
     * explore view.
     */
    List<Playlist> findByIsPublicTrue();
}
