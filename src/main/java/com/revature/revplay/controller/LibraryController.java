package com.revature.revplay.controller;

import com.revature.revplay.dto.PlaylistDto;
import com.revature.revplay.entity.Playlist;
import com.revature.revplay.entity.Song;
import com.revature.revplay.service.PlaylistService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Set;

/**
 * This controller serves as the primary hub for managing a user's personal
 * music library.
 * It handles the display and organization of liked songs, custom playlists, and
 * track management.
 * By mapping requests under "/library", it provide a centralized location for
 * users to curate
 * their listening experience. The class interacts with the PlaylistService to
 * bridge the
 * gap between the user interface and the underlying database relationships for
 * music curation.
 */
@Controller
@RequestMapping("/library")
@Log4j2
public class LibraryController {

    private final PlaylistService playlistService;

    /**
     * Standard constructor for injecting the PlaylistService dependency.
     * 
     * This injection is critical because:
     * 1. It allows the controller to delegate all data operations to a specialized
     * service.
     * 2. It maintains the separation of concerns between web routing and business
     * logic.
     * 3. It provides access to complex operations like toggling likes and building
     * playlists.
     * 4. This follows the industry standard Dependency Injection pattern for clean,
     * testable code.
     */
    public LibraryController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    /**
     * Renders the "Liked Songs" page where users can see all tracks they've
     * favorited.
     * 
     * The process for displaying liked songs involves:
     * 1. Retrieving the identity of the current user via the Authentication object.
     * 2. Querying the service layer for the full set of songs the user has marked
     * with a heart.
     * 3. Passing this collection to the 'Model' for the Thymeleaf view to render.
     * 4. Returning the "library/liked" template name to show the dedicated
     * favorites list.
     * 5. This method acts as the user's "Home Base" for the songs they love most.
     */
    @GetMapping("/liked")
    public String viewLikedSongs(Authentication authentication, Model model) {
        log.info("User {} is viewing their liked songs", authentication.getName());
        Set<Song> likedSongs = playlistService.getLikedSongs(authentication.getName());
        model.addAttribute("songs", likedSongs);
        return "library/liked";
    }

    /**
     * An asynchronous endpoint to toggle the 'Like' status of a specific song.
     * 
     * This API method performs the following:
     * 1. Verifies that the user is currently logged in to prevent unauthorized
     * social actions.
     * 2. Calls the service to either add or remove the song from the user's
     * favorites list.
     * 3. Returns a boolean response indicating the new liked state of the song.
     * 4. This enables a snappy, real-time UI where heart icons update without a
     * page refresh.
     * 5. It uses @ResponseBody to send a direct JSON-style response to the
     * front-end scripts.
     */
    @PostMapping("/like/{songId}")
    @ResponseBody
    public ResponseEntity<Boolean> toggleLike(@PathVariable("songId") Long songId) {
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()
                || auth instanceof org.springframework.security.authentication.AnonymousAuthenticationToken) {
            log.warn("Unauthorized attempt to like song ID: {}", songId);
            return ResponseEntity.status(401).build();
        }
        log.info("User {} is toggling like for song ID: {}", auth.getName(), songId);
        boolean isNowLiked = playlistService.toggleLikeSong(songId, auth.getName());
        return ResponseEntity.ok(isNowLiked);
    }

    /**
     * Checks the current like status for a song to properly render front-end icons.
     * 
     * This utility API method handles:
     * 1. Identifying if the requester is an authenticated user.
     * 2. Returning 'false' immediately if an anonymous user is browsing.
     * 3. querying the service for a quick check of the user's favorite list.
     * 4. Returning the result as a raw boolean value.
     * 5. This ensures that when a song starts playing, the UI correctly shows if
     * it's already liked.
     */
    @GetMapping("/like/status/{songId}")
    @ResponseBody
    public ResponseEntity<Boolean> getLikeStatus(@PathVariable("songId") Long songId) {
        log.debug("Checking like status for song ID: {}", songId);
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()
                || auth instanceof org.springframework.security.authentication.AnonymousAuthenticationToken) {
            return ResponseEntity.ok(false);
        }
        boolean isLiked = playlistService.isSongLiked(songId, auth.getName());
        return ResponseEntity.ok(isLiked);
    }

    /**
     * Provides a JSON list of the user's playlists for dynamic UI components.
     * 
     * This API endpoint is useful for:
     * 1. Populating the "Add to Playlist" context menu throughout the app.
     * 2. Returning a lightweight list containing only names and IDs for fast
     * processing.
     * 3. Allowing JavaScript components to interact with the user's collections.
     * 4. Handling unauthorized access by returning a 401 status code if the user
     * isn't logged in.
     */
    @GetMapping("/api/playlists")
    @ResponseBody
    public ResponseEntity<java.util.List<java.util.Map<String, Object>>> getPlaylistsJson(
            Authentication authentication) {
        log.debug("Fetching playlists JSON for user: {}",
                authentication != null ? authentication.getName() : "anonymous");
        if (authentication == null)
            return ResponseEntity.status(401).build();
        java.util.List<Playlist> playlists = playlistService.getUserPlaylists(authentication.getName());
        java.util.List<java.util.Map<String, Object>> result = playlists.stream().map(p -> {
            java.util.Map<String, Object> map = new java.util.HashMap<>();
            map.put("id", p.getId());
            map.put("name", p.getName());
            return map;
        }).collect(java.util.stream.Collectors.toList());
        return ResponseEntity.ok(result);
    }

    /**
     * Displays the main playlists management page for the user.
     * 
     * The logic for this view method includes:
     * 1. Fetching every collection the user has created.
     * 2. Injecting a fresh PlaylistDto into the model to prepare the "Create New"
     * form.
     * 3. Returning the "library/playlists" view for the browser to render the
     * dashboard.
     * 4. This is the starting point for users wanting to organize their personal
     * library structure.
     */
    @GetMapping("/playlists")
    public String viewPlaylists(Authentication authentication, Model model) {
        log.info("User {} is viewing their playlists dashboard", authentication.getName());
        model.addAttribute("playlists", playlistService.getUserPlaylists(authentication.getName()));
        model.addAttribute("playlistDto", new PlaylistDto());
        return "library/playlists";
    }

    /**
     * Processes the creation of a brand new playlist from a form submission.
     * 
     * This method handles:
     * 1. Capturing the name and description from the incoming form-backing DTO.
     * 2. Calling the service to create the record tied to the current username.
     * 3. Adding a success flash attribute to notify the user via a temporary
     * banner.
     * 4. Redirecting back to the playlists list to prevent duplicate form
     * submissions.
     */
    @PostMapping("/playlists/create")
    public String createPlaylist(@ModelAttribute("playlistDto") PlaylistDto playlistDto,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        log.info("User {} is creating playlist: '{}'", authentication.getName(), playlistDto.getName());
        playlistService.createPlaylist(playlistDto, authentication.getName());
        redirectAttributes.addFlashAttribute("successMessage", "Playlist created successfully.");
        return "redirect:/library/playlists";
    }

    /**
     * Shows the full track list and metadata for a specific playlist ID.
     * 
     * The detail view process involves:
     * 1. fetching the full Playlist entity (including its songs) from the database.
     * 2. passing the populated object to the "library/playlist-detail" template.
     * 3. This page allows users to listen to the playlist or manage individual
     * tracks.
     * 4. It acts as the "Deeper Dive" view for a user's curated music sets.
     */
    @GetMapping("/playlists/{id}")
    public String viewPlaylistDetails(@PathVariable("id") Long id, Model model) {
        log.info("Viewing playlist details for ID: {}", id);
        Playlist playlist = playlistService.getPlaylistById(id);
        model.addAttribute("playlist", playlist);

        PlaylistDto playlistDto = new PlaylistDto();
        playlistDto.setId(playlist.getId());
        playlistDto.setName(playlist.getName());
        playlistDto.setDescription(playlist.getDescription());
        playlistDto.setPublic(playlist.isPublic());
        model.addAttribute("playlistDto", playlistDto);

        return "library/playlist-detail";
    }

    /**
     * Processes the update of an existing playlist from a form submission.
     */
    @PostMapping("/playlists/{id}/update")
    public String updatePlaylist(@PathVariable("id") Long id,
            @ModelAttribute("playlistDto") PlaylistDto playlistDto,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        log.info("User {} is updating playlist ID: '{}'", authentication.getName(), id);
        playlistService.updatePlaylist(id, playlistDto, authentication.getName());
        redirectAttributes.addFlashAttribute("successMessage", "Playlist updated successfully.");
        return "redirect:/library/playlists/" + id;
    }

    /**
     * Handles the permanent removal of a playlist.
     * 
     * The deletion workflow includes:
     * 1. Triggering the delete operation in the service layer using the playlist ID
     * and user identity.
     * 2. Adding a feedback message to let the user know the operation was
     * successful.
     * 3. Redirecting back to the overview page for a clean navigation experience.
     * 4. This method helps users keep their library focused only on the music they
     * still want.
     */
    @PostMapping("/playlists/{id}/delete")
    public String deletePlaylist(@PathVariable("id") Long id, Authentication authentication,
            RedirectAttributes redirectAttributes) {
        log.info("User {} is deleting playlist ID: {}", authentication.getName(), id);
        playlistService.deletePlaylist(id, authentication.getName());
        redirectAttributes.addFlashAttribute("successMessage", "Playlist deleted.");
        return "redirect:/library/playlists";
    }

    /**
     * An API endpoint to add a track to a specific playlist dynamically.
     * 
     * This asynchronous method is used for:
     * 1. Handling "Add to Playlist" clicks from anywhere in the application.
     * 2. Checking authorization to ensure only the owner can modify their own
     * lists.
     * 3. returning a raw text response to the JavaScript caller.
     * 4. This enables users to organize music while they are currently listening to
     * a song.
     */
    @PostMapping("/api/playlists/{playlistId}/add/{songId}")
    @ResponseBody
    public ResponseEntity<String> addSongToPlaylist(@PathVariable("playlistId") Long playlistId,
            @PathVariable("songId") Long songId,
            Authentication authentication) {
        log.info("Request to add song ID: {} to playlist ID: {}", songId, playlistId);
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("Authentication required");
        }
        playlistService.addSongToPlaylist(playlistId, songId, authentication.getName());
        return ResponseEntity.ok("Added completely!");
    }

    /**
     * Removes a particular track from a playlist view.
     * 
     * This method manages:
     * 1. Calling the service to sever the link between the song and the specific
     * collection.
     * 2. Adding a notification message for the user.
     * 3. Redirecting back to the same detail page to show the updated track list.
     * 4. It allows for quick curation and editing of a playlist's contents.
     */
    @PostMapping("/api/playlists/{playlistId}/remove/{songId}")
    public String removeSongFromPlaylistNative(@PathVariable("playlistId") Long playlistId,
            @PathVariable("songId") Long songId,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        log.info("User {} is removing song ID: {} from playlist ID: {}", authentication.getName(), songId, playlistId);
        playlistService.removeSongFromPlaylist(playlistId, songId, authentication.getName());
        redirectAttributes.addFlashAttribute("successMessage", "Song removed from playlist.");
        return "redirect:/library/playlists/" + playlistId;
    }
    /**

    * Allows a user to follow a public playlist.
      */
      @PostMapping("/playlists/{id}/follow")
      public String followPlaylist(@PathVariable("id") Long id,
      Authentication authentication,
      RedirectAttributes redirectAttributes) {

      log.info("User {} is following playlist ID: {}", authentication.getName(), id);

      playlistService.followPlaylist(id, authentication.getName());

      redirectAttributes.addFlashAttribute("successMessage", "You are now following this playlist.");

      return "redirect:/library/playlists/" + id;
      }

    /**

    * Allows a user to unfollow a playlist.
      */
      @PostMapping("/playlists/{id}/unfollow")
      public String unfollowPlaylist(@PathVariable("id") Long id,
      Authentication authentication,
      RedirectAttributes redirectAttributes) {

      log.info("User {} is unfollowing playlist ID: {}", authentication.getName(), id);

      playlistService.unfollowPlaylist(id, authentication.getName());

      redirectAttributes.addFlashAttribute("successMessage", "You unfollowed this playlist.");

      return "redirect:/library/playlists/" + id;
      }

    /**

    * API endpoint to fetch playlist follower count.
      */
      @GetMapping("/api/playlists/{id}/followers")
      @ResponseBody
      public ResponseEntity<Long> getPlaylistFollowerCount(@PathVariable("id") Long id) {

      log.debug("Fetching follower count for playlist ID: {}", id);

      long count = playlistService.getPlaylistFollowerCount(id);

      return ResponseEntity.ok(count);
      }

}