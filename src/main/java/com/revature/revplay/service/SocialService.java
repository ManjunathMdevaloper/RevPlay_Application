package com.revature.revplay.service;

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



    boolean toggleFollowArtist(Long artistId, String username);

    /**
     * Checks the real-time social connection status between two accounts.
     *
     * The relationship check handles:
     * 1. Querying the join table to see if the listener currently follows the
     * artist.
     * 2. returning a boolean flag that determines the state of the "Follow" button.
     * 3. This is essential for personalizing the artist's public profile page.
     * 4. It ensures users have immediate visual feedback on their social status.
     */
    boolean isFollowing(Long artistId, String username);



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



    long getFollowerCount(Long artistId);

    /**
     * Retrieves a detailed list of the users who comprise an artist's audience.
     *
     * The audience lookup entails:
     * 1. Fetching all User entities that have a 'FOLLOW' relationship with target
     * ID.
     * 2. help the platform build community-focused views like "Top Fans" or
     * follower lists.
     * 3. allowing artists to see exactly who is engaging with their musical output.
     * 4. This drives networking and social transparency across the RevPlay
     * ecosystem.
     */
    List<User> getFollowers(Long artistId);



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

