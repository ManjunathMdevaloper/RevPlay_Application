package com.revature.revplay.repository;

import com.revature.revplay.entity.History;
import com.revature.revplay.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This repository interface manages the database persistence for personal
 * listening histories.
 * It provides the primary data access layer for tracking every musical track a
 * user
 * enjoys, capturing the precise chronological order of playback.
 * By extending JpaRepository, it allows for efficient retrieval of a user's
 * "Recently Played" timeline and supports bulk deletion for privacy-focused
 * history resets.
 * This is the core component for personalizing the listener's journey on
 * RevPlay.
 */
@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {
    /**
     * Retrieves the complete listening timeline for a specific account, sorted by
     * recent activity.
     * This provides the data for the "History" dashboard, letting users revisit
     * their favorite songs.
     */
    List<History> findByUserOrderByPlayedAtDesc(User user);

    /**
     * Performs an atomic purge of all listening records associated with a specific
     * user.
     * This is used by the "Clear History" feature to protect user privacy.
     */
    void deleteByUser(User user);

    /**
     * Removes all historical records linked to a specific track.
     * This is a critical cleanup step performed when an artist chooses to delete a
     * song.
     */
    void deleteBySong(com.revature.revplay.entity.Song song);
}
