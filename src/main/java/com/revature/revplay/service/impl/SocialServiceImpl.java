
package com.revature.revplay.service.impl;

import com.revature.revplay.entity.Song;
import com.revature.revplay.entity.User;
import com.revature.revplay.exception.ResourceNotFoundException;
import com.revature.revplay.repository.SongRepository;
import com.revature.revplay.repository.UserRepository;
import com.revature.revplay.service.SocialService;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This class provides the concrete implementation for the platform's social and
 * community logic.
 * It manages the persistent relationships between listeners and artists,
 * ensuring that
 * the social graph is correctly stored and retrieved from the database.
 * Additionally, it handles the aggregation of platform-wide trends, such as
 * top-played
 * tracks and most-streamed artists, to drive the discovery charts.
 * By using transactional methods, it guarantees that social updates are atomic
 * and reliable.
 */
@Service
public class SocialServiceImpl implements SocialService {

    private final UserRepository userRepository;
    private final SongRepository songRepository;

    /**
     * Standard constructor that wires up core repositories for social data
     * management.
     *
     * These dependencies allow the service to:
     * 1. Access user account status and role-based permissions (Artist vs. User).
     * 2. verify following relationships through the standard user database.
     * 3. Pull play-count analytics from the song library for trending charts.
     * 4. Perform complex stream aggregations for artist dashboards.
     * 5. This setup ensures that social interactions are grounded in the platform's
     * actual data.
     */
    public SocialServiceImpl(UserRepository userRepository, SongRepository songRepository) {
        this.userRepository = userRepository;
        this.songRepository = songRepository;
    }

    /**
     * Atomically toggles the "Follow" state between a user and a target artist.
     *
     * The following logic sequence handles:
     * 1. Validating that both the requesting user and the target artist exist in
     * the system.
     * 2. Security: preventing users from following their own accounts to avoid
     * metric manipulation.
     * 3. checking the current relationship status through a robust database count.
     * 4. Adding or removing the artist from the user's "Following" collection as
     * appropriate.
     * 5. returning the final relationship state to help the UI update instantly.
     */
    @Override
    @Transactional
    public boolean toggleFollowArtist(Long artistId, String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        User artist = userRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found"));

        if (user.getId().equals(artist.getId())) {
            throw new RuntimeException("You cannot follow yourself");
        }

        // Use more robust check
        boolean isFollowing = userRepository.countFollowersForUser(username, artistId) > 0;

        if (isFollowing) {
            user.getFollowing().remove(artist);
            userRepository.save(user);
            return false;
        } else {
            user.getFollowing().add(artist);
            userRepository.save(user);
            return true;
        }
    }

    /**
     * Checks if a specific user is currently following a target artist.
     *
     * The relationship check process:
     * 1. Safely handling cases where the username might be null (unauthenticated
     * guests).
     * 2. performing a high-performance count query on the social join table.
     * 3. Returning true if a link exists, ensuring the UI "Follow" buttons are
     * accurate.
     * 4. This method is optimized for frequent read access during profile browsing.
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isFollowing(Long artistId, String username) {
        if (username == null)
            return false;
        return userRepository.countFollowersForUser(username, artistId) > 0;
    }

    /**
     * Calculates the total size of an artist's audience on the platform.
     *
     * The count retrieval logic:
     * 1. querying the specialized repository method that counts followers by artist
     * project ID.
     * 2. Providing a numeric metric of influence for use on public profile pages.
     * 3. ensuring that the count is real-time and reflects current user
     * connections.
     * 4. This is a primary vanity metric for creators and a discovery hint for
     * listeners.
     */
    @Override
    @Transactional(readOnly = true)
    public long getFollowerCount(Long artistId) {
        return userRepository.countFollowersByArtistId(artistId);
    }

    /**
     * Fetches the complete list of users who are members of an artist's audience.
     *
     * The audience lookup process:
     * 1. retrieving all 'User' records that have established a follow link to the
     * artist.
     * 2. enabling community features such as "Fans" lists or notification targets.
     * 3. providing transparency to artists about who is supporting their musical
     * career.
     * 4. ensuring that social connections are bidirectional and explorable.
     */
    @Override
    @Transactional(readOnly = true)
    public List<User> getFollowers(Long artistId) {
        return userRepository.findFollowersByArtistId(artistId);
    }

    /**
     * Identifies the music that is currently capturing the community's attention.
     *
     * The trending aggregation sequence:
     * 1. querying the song repository for tracks with high play counts and recent
     * activity.
     * 2. applying a page-based limit to return only the top 'n' hottest tracks.
     * 3. ensuring the "Trending" view stays fresh and reflects current platform
     * usage.
     * 4. This is the primary driver of passive music discovery for new users.
     */
    @Override
    @Transactional(readOnly = true)
    public List<Song> getTopTrendingSongs(int limit) {
        return songRepository.findTopTrendingSongs(PageRequest.of(0, limit));
    }

    /**
     * Ranks the platform's professional creators by their cumulative listening
     * reach.
     *
     * The artist chart logic manages:
     * 1. fetching every verified artist account from the master user table.
     * 2. sorting them through a heavy-duty stream comparison that sums all their
     * track plays.
     * 3. applying a result limit to create a "Top Artists" leaderboard for the
     * dashboard.
     * 4. highlighting the most successful creators to encourage healthy platform
     * competition.
     * 5. helping users find established names in the RevPlay ecosystem.
     */
    @Override
    @Transactional(readOnly = true)
    public List<User> getTopArtists(int limit) {
        // Simple logic: Get artists and sort locally by their total streams (can be
        // optimized if massive dataset)
        List<User> artists = userRepository.findAllArtists();
        return artists.stream()
                .sorted((a1, a2) -> {
                    Long s1 = songRepository.getTotalPlayCountByArtistId(a1.getId());
                    Long s2 = songRepository.getTotalPlayCountByArtistId(a2.getId());
                    return Long.compare(s2 != null ? s2 : 0L, s1 != null ? s1 : 0L);
                })
                .limit(limit)
                .collect(Collectors.toList());
    }

    /**
     * Aggregates the lifetime stream metrics for an artist's entire musical
     * portfolio.
     *
     * The stream reporting logic:
     * 1. Querying the song database for the Sum of plays across all songs by the
     * artist ID.
     * 2. Safely handling null values to return a clean zero count if no plays
     * exist.
     * 3. providing the "Total Reached" analytic for the private artist dashboard.
     * 4. ensuring that creators can track their growth milestones accurately over
     * time.
     */
    @Override
    @Transactional(readOnly = true)
    public long getTotalArtistStreams(Long artistId) {
        Long total = songRepository.getTotalPlayCountByArtistId(artistId);
        return total != null ? total : 0L;
    }
}


