package com.revature.revplay.repository;

import com.revature.revplay.entity.Album;
import com.revature.revplay.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This repository interface manages the database persistence for professional
 * musical projects (Albums).
 * It provides the primary data access layer for retrieving curated collections
 * of songs,
 * including support for keyword-based project searches and artist-specific
 * discography lookups.
 * By extending JpaRepository, it ensures that every album record is reliably
 * stored,
 * updated, or removed according to the creator's needs.
 * This is the core component for organizing the platform's music into official
 * releases.
 */
@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {
    /**
     * Performs a broad, case-insensitive search for album projects by their name.
     * This is used by the discovery engine to find relevant collections for the
     * user.
     */
    List<Album> findByNameContainingIgnoreCase(String name);

    /**
     * Retrieves the entire professional catalog released by a specific music
     * creator.
     * Used for building the artist's public profile and their private creator
     * dashboard.
     */
    List<Album> findByArtist(User artist);
}