package com.revature.revplay.service;

import com.revature.revplay.dto.SearchResultDto;
import com.revature.revplay.entity.Song;

import java.util.List;

/**
 * This service interface defines the contract for the platform's music
 * discovery engine.
 * It provides methods for searching across the entire ecosystem—songs, artists,
 * albums, and playlists—with high efficiency and accuracy.
 * By defining methods for keyword-based search and granular filtering, it
 * enables
 * users to find exactly the music they are looking for with minimal effort.
 * This acts as the search backbone that powers both the search bar and category
 * browsers.
 */
public interface SearchService {

    /**
     * Performs a comprehensive search across all major entity types in the RevPlay
     * system.
     * The searching logic includes:
     * 1. Accepting a raw keyword string from the user's search entry.
     * 2. checking multiple fields simultaneously, such as names, titles, and
     * descriptions.
     * 3. Aggregating those broad matches into a single SearchResultDto for easy
     * display.
     * 4. Ensuring that common search terms return the most relevant entities first.
     * 5. This method is the primary driver of the "Explore" functionality for
     * discovery.
     */
    SearchResultDto searchAll(String keyword);

    /**
     * Applies a specialized filter set to the global song catalog for refined
     * finding.
     * The filtering implementation handles:
     * 1. Combining multiple optional criteria like artist identity, genre, and
     * album links.
     * 2. Narrowing down the song list based on chronological clues like release
     * years.
     * 3. performing dynamic queries that build themselves based on only the
     * provided inputs.
     * 4. enabling artists or listeners to find specific sub-sections of a large
     * library.
     * 5. This ensures that the music catalog remains manageable and navigable as it
     * grows.
     */
    List<Song> filterSongs(String title, String genre, Long artistId, Long albumId, Integer releaseYear);

    /**
     * Retrieves a curated list of every unique genre currently present in the
     * system.
     * This metadata retrieval process:
     * 1. Scans the entire song database for distinct genre tags.
     * 2. returns a clean list of categorizeable strings to populate filter
     * dropdowns.
     * 3. helps users browse the collection by their preferred musical styles.
     * 4. This list is essential for building the "Category" or "Mood" sections of
     * the UI.
     * 5. ensuring that the browsing experience is always based on actual available
     * content.
     */
    List<String> getAllGenres();
}