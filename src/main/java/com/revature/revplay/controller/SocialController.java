package com.revature.revplay.controller;

import com.revature.revplay.service.SocialService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * This controller serves as the social hub of the RevPlay platform, managing
 * interpersonal
 * connections, audience engagement, and personal listening analytics.
 * It handles the "Social Graph" by allowing users to follow their favorite
 * artists and
 * tracks the community's trending tastes through popularity-based leaderboards.
 * Additionally, it provides personal transparency by managing the user's
 * private listening
 * history, allowing for reflection and account organization.
 * It acts as the primary interface for any feature that builds a sense of
 * community.
 */
@Controller
@RequestMapping("/social")
public class SocialController {

    private final SocialService socialService;
    private final com.revature.revplay.repository.HistoryRepository historyRepository;
    private final com.revature.revplay.repository.UserRepository userRepository;

    /**
     * Standard constructor that wires up social services and data repositories.
     * 
     * The dependencies provided here enable:
     * 1. Toggling following/unfollowing relationships between listeners and
     * artists.
     * 2. checking real-time social status to update UI follow buttons.
     * 3. Managing the lifecycle of personal listening records (History).
     * 4. Aggregating platform-wide data to determine what music is currently
     * "Trending".
     * 5. This setup ensures that social interactions are fast, secure, and
     * data-consistent.
     */
    public SocialController(SocialService socialService,
            com.revature.revplay.repository.HistoryRepository historyRepository,
            com.revature.revplay.repository.UserRepository userRepository) {
        this.socialService = socialService;
        this.historyRepository = historyRepository;
        this.userRepository = userRepository;
    }

    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(SocialController.class);

    /**
     * Toggles the "Follow" relationship between the current user and a target
     * artist.
     * 
     * The following logic performs:
     * 1. Validating that the requesting user is authenticated; otherwise, returning
     * a 401 error.
     * 2. Handing off the logic to the SocialService to atomcially add or remove the
     * relationship.
     * 3. Returning a boolean status to the front-end so the button UI can update in
     * real-time.
     * 4. Error handling with logging to ensure platform stability during social
     * bursts.
     * 5. This is the primary driver of artist-fan engagement on the platform.
     */

    @PostMapping("/follow/{artistId}")
    @ResponseBody
    public ResponseEntity<Boolean> toggleFollow(@PathVariable("artistId") Long artistId,
                                                Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.status(401).build();
            }
            boolean isNowFollowing = socialService.toggleFollowArtist(artistId, authentication.getName());
            return ResponseEntity.ok(isNowFollowing);
        } catch (Exception e) {
            log.error("Toggle follow failed for artistId {}: ", artistId, e);
            return ResponseEntity.status(500).body(false);
        }
    }

    /**
     * Retrieves the current relationship status between a viewer and an artist.
     *
     * The status check handles:
     * 1. Identifying the authenticated user from the Spring Security context.
     * 2. querying the social graph to see if a 'FOLLOW' record exists for this
     * artist pairing.
     * 3. returning a raw boolean result to help the detail page render the correct
     * button state.
     * 4. This ensures that the user interface always reflects the true state of the
     * user's network.
     * 5. It is used frequently during page loads to personalize the artist
     * discovery experience.
     */
    @GetMapping("/follow/status/{artistId}")
    @ResponseBody
    public ResponseEntity<Boolean> getFollowStatus(@PathVariable("artistId") Long artistId,
                                                   Authentication authentication) {
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                return ResponseEntity.ok(false);
            }
            boolean isFollowing = socialService.isFollowing(artistId, authentication.getName());
            return ResponseEntity.ok(isFollowing);
        } catch (Exception e) {
            log.error("Follow status check failed for artistId {}: ", artistId, e);
            return ResponseEntity.status(500).body(false);
        }
    }
/**
     * Renders a dashboard of the most popular content currently on the platform.
     * 
     * The trending logic entails:
     * 1. Fetching a curated list of the top 20 most-played songs across the entire
     * system.
     * 2. Aggregating and displaying the top 10 artists with the most active
     * followers.
     * 3. Injecting these datasets into the "discovery/trending" view for user-wide
     * exposure.
     * 4. This method powers the "community pulse" and helps new users find popular
     * music quickly.
     * 5. It drives platform discovery by highlighting what other peers are
     * enjoying.
     */

@GetMapping("/trending")
    public String viewTrending(Model model) {
        model.addAttribute("topSongs", socialService.getTopTrendingSongs(20));
        model.addAttribute("topArtists", socialService.getTopArtists(10));
        return "discovery/trending";
    }

/**
     * Displays a chronological list of every track the user has recently enjoyed.
     * 
     * The history retrieval process includes:
     * 1. Enforcing authentication to ensure private listening data remains strictly
     * confidential.
     * 2. Finding the user account and pulling all history events sorted by the
     * 'playedAt' timestamp.
     * 3. presenting an organized timeline of tracks for the user to review or
     * re-play.
     * 4. Returning the "discovery/history" view as a personal audit trail for the
     * listener.
     * 5. This adds a layer of personalization and convenience to the RevPlay
     * experience.
     */


@GetMapping("/history")
public String viewHistory(Authentication authentication, Model model) {
    if (authentication == null)
        return "redirect:/login";

    com.revature.revplay.entity.User user = userRepository.findByUsername(authentication.getName()).orElseThrow();
    java.util.List<com.revature.revplay.entity.History> allHistory = historyRepository
            .findByUserOrderByPlayedAtDesc(user);

    // Recent history: last 50 songs
    java.util.List<com.revature.revplay.entity.History> recentHistory = allHistory.stream()
            .limit(50)
            .collect(java.util.stream.Collectors.toList());

    model.addAttribute("recentHistory", recentHistory);
    model.addAttribute("completeHistory", allHistory);
    // Keep 'history' for backward compatibility if any other fragment uses it
    model.addAttribute("history", recentHistory);

    return "discovery/history";
}
    

/**
     * Performs a complete reset of the user's listening chronological records.
     * 
     * the history purge logic manages:
     * 1. Security check to verify the user is acting on their own account session.
     * 2. triggering a transactional database deletion of all history rows linked to
     * the user.
     * 3. cleaning up the user's digital footprint to provide a "fresh start" for
     * their library.
     * 4. Redirecting back to the history view to show the now-empty state.
     * 5. This is a privacy-first feature that gives users full control over their
     * account data.
     */


@PostMapping("/history/clear")
@org.springframework.transaction.annotation.Transactional
public String clearHistory(Authentication authentication) {
    if (authentication == null)
        return "redirect:/login";
    com.revature.revplay.entity.User user = userRepository.findByUsername(authentication.getName()).orElseThrow();
    historyRepository.deleteByUser(user);
    return "redirect:/social/history";
}



}
