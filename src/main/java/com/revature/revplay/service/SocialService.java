package com.revature.revplay.service;

import com.revature.revplay.dto.SearchResultDto;
import com.revature.revplay.entity.Song;
import com.revature.revplay.entity.User;

import java.util.List;

/**
 * This service interface defines the contract for all social and
 * community-driven features.
 * It governs the relationship graph between users and creators, as well as the
 * platform-wide popularity analytics that drive the charts and trending
 * sections.
 * By defining methods for following, follower counting, and trending
 * aggregation,
 * it ensures a consistent and socially-rich environment for music discovery.
 * This is the primary engine for building a sense of community and artist
 * loyalty.
 */
public interface SocialService {

    /**
     * Toggles the connection between a listener and a professional musical creator.
     * 
     * The following logic implementation:
     * 1. Check if a "Follow" record already exists in the social database for this
     * pair.
     * 2. If it does, safely remove it to "Unfollow" the artist.
     * 3. If it doesn't, create a new relationship to "Follow" the artist.
     * 4. Returns the final state (true for following, false for not) to the caller.
     * 5. This atomicity ensures the social graph remains synchronized with UI
     * buttons.
     */

    public interface SearchService {

        /**
         * Performs a comprehensive search across all major entity types in the RevPlay
         * system.
         *
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
         *
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
         *
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

/**
     * Aggregates the total audience size for a specific music creator.
     * 
     * The count retrieval process:
     * 1. performs a high-performance count query on the social relationship table.
     * 2. returns the raw number of unique users who have chosen to follow this
     * artist.
     * 3. This metric is a key indicator of an artist's popularity and platform
     * reach.
     * 4. Used for both public profile displays and internal ranking algorithms.
     */

// ####################################### Person5 CODE START #########################################
    
// ######################################## Person5 CODE END ##########################################
/**
     * Calculates and returns the most popular tracks on the platform within a
     * limit.
     * 
     * The trending song logic includes:
     * 1. Analyzing play counts and historical listening events to identify "hot"
     * tracks.
     * 2. Sorting the global catalog by recent popularity and raw play frequency.
     * 3. Limiting the result set to the top 'n' items to keep charts concise and
     * relevant.
     * 4. This powers the "Trending Today" and platforms-wide hit lists for users.
     */
List<Song> getTopTrendingSongs(int limit);

    /**
     * Ranks the top artists based on their total community audience and engagement.
     * 
     * The artist ranking process:
     * 1. evaluating creators by their total follower counts and aggregate stream
     * metrics.
     * 2. returning the most influential artists to showcase on the trending
     * dashboards.
     * 3. Helping new users find established and trending creators immediately.
     * 4. This drives artist discovery and highlights successful platform members.
     */
    List<User> getTopArtists(int limit);
    

/**
     * Calculates the total lifetime listening reach of an artist's entire catalog.
     * 
     * The stream aggregation logic:
     * 1. Summing the play counts for every individual song associated with the
     * artist.
     * 2. Providing a single numeric value representing the creator's total impact.
     * 3. This is the primary metric for artist dashboards and growth analytic
     * charts.
     * 4. It ensures creators can track their cumulative progress over time.
     */

long getTotalArtistStreams(Long artistId);

}
