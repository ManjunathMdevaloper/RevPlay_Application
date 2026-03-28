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

@Controller
@RequestMapping("/library")
@Log4j2
public class LibraryController {

    private final PlaylistService playlistService;

    public LibraryController(PlaylistService playlistService) {
        this.playlistService = playlistService;
    }

    @GetMapping("/liked")
    public String viewLikedSongs(Authentication authentication, Model model) {
        log.info("User {} is viewing their liked songs", authentication.getName());
        Set<Song> likedSongs = playlistService.getLikedSongs(authentication.getName());
        model.addAttribute("songs", likedSongs);
        return "library/liked";
    }

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

    @GetMapping("/playlists")
    public String viewPlaylists(Authentication authentication, Model model) {
        log.info("User {} is viewing their playlists dashboard", authentication.getName());
        model.addAttribute("playlists", playlistService.getUserPlaylists(authentication.getName()));
        model.addAttribute("playlistDto", new PlaylistDto());
        return "library/playlists";
    }

    @PostMapping("/playlists/create")
    public String createPlaylist(@ModelAttribute("playlistDto") PlaylistDto playlistDto,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {
        log.info("User {} is creating playlist: '{}'", authentication.getName(), playlistDto.getName());
        playlistService.createPlaylist(playlistDto, authentication.getName());
        redirectAttributes.addFlashAttribute("successMessage", "Playlist created successfully.");
        return "redirect:/library/playlists";
    }

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

    @PostMapping("/playlists/{id}/delete")
    public String deletePlaylist(@PathVariable("id") Long id, Authentication authentication,
            RedirectAttributes redirectAttributes) {
        log.info("User {} is deleting playlist ID: {}", authentication.getName(), id);
        playlistService.deletePlaylist(id, authentication.getName());
        redirectAttributes.addFlashAttribute("successMessage", "Playlist deleted.");
        return "redirect:/library/playlists";
    }

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
      @PostMapping("/playlists/{id}/follow")
      public String followPlaylist(@PathVariable("id") Long id,
      Authentication authentication,
      RedirectAttributes redirectAttributes) {

      log.info("User {} is following playlist ID: {}", authentication.getName(), id);

      playlistService.followPlaylist(id, authentication.getName());

      redirectAttributes.addFlashAttribute("successMessage", "You are now following this playlist.");

      return "redirect:/library/playlists/" + id;
      }

      @PostMapping("/playlists/{id}/unfollow")
      public String unfollowPlaylist(@PathVariable("id") Long id,
      Authentication authentication,
      RedirectAttributes redirectAttributes) {

      log.info("User {} is unfollowing playlist ID: {}", authentication.getName(), id);

      playlistService.unfollowPlaylist(id, authentication.getName());

      redirectAttributes.addFlashAttribute("successMessage", "You unfollowed this playlist.");

      return "redirect:/library/playlists/" + id;
      }

      @GetMapping("/api/playlists/{id}/followers")
      @ResponseBody
      public ResponseEntity<Long> getPlaylistFollowerCount(@PathVariable("id") Long id) {

      log.debug("Fetching follower count for playlist ID: {}", id);

      long count = playlistService.getPlaylistFollowerCount(id);

      return ResponseEntity.ok(count);
      }

}
