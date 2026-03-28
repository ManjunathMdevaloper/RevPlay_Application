package com.revature.revplay.controller;

import com.revature.revplay.service.SocialService;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;


@Controller
@RequestMapping("/social")
@Log4j2
public class SocialController {

    private final SocialService socialService;
    private final com.revature.revplay.repository.HistoryRepository historyRepository;
    private final com.revature.revplay.repository.UserRepository userRepository;

    
    public SocialController(SocialService socialService,
            com.revature.revplay.repository.HistoryRepository historyRepository,
            com.revature.revplay.repository.UserRepository userRepository) {
        this.socialService = socialService;
        this.historyRepository = historyRepository;
        this.userRepository = userRepository;
    }

    
    @PostMapping("/follow/{artistId}")
    @ResponseBody
    public ResponseEntity<Boolean> toggleFollow(@PathVariable("artistId") Long artistId,
            Authentication authentication) {
        log.info("Attempting to toggle follow for artistId: {} by user: {}", artistId,
                authentication != null ? authentication.getName() : "Anonymous");
        try {
            if (authentication == null || !authentication.isAuthenticated()) {
                log.warn("Unauthorized attempt to toggle follow for artistId: {}", artistId);
                return ResponseEntity.status(401).build();
            }
            boolean isNowFollowing = socialService.toggleFollowArtist(artistId, authentication.getName());
            log.info("Toggle follow successful. New status for artistId {}: following={}", artistId, isNowFollowing);
            return ResponseEntity.ok(isNowFollowing);
        } catch (Exception e) {
            log.error("Toggle follow failed for artistId {}: ", artistId, e);
            return ResponseEntity.status(500).body(false);
        }
    }

    
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

    
    @GetMapping("/trending")
    public String viewTrending(Model model) {
        log.info("Request for trending content page");
        model.addAttribute("topSongs", socialService.getTopTrendingSongs(20));
        model.addAttribute("topArtists", socialService.getTopArtists(10));
        return "discovery/trending";
    }

    
    @GetMapping("/history")
    public String viewHistory(Authentication authentication, Model model) {
        log.info("Request for listening history by user: {}",
                authentication != null ? authentication.getName() : "anonymous");
        if (authentication == null)
            return "redirect:/login";

        com.revature.revplay.entity.User user = userRepository.findByUsername(authentication.getName()).orElseThrow();
        java.util.List<com.revature.revplay.entity.History> allHistory = historyRepository
                .findByUserOrderByPlayedAtDesc(user);

        
        java.util.List<com.revature.revplay.entity.History> recentHistory = allHistory.stream()
                .limit(50)
                .collect(java.util.stream.Collectors.toList());

        model.addAttribute("recentHistory", recentHistory);
        model.addAttribute("completeHistory", allHistory);
        
        model.addAttribute("history", recentHistory);

        return "discovery/history";
    }

    
    @PostMapping("/history/clear")
    @org.springframework.transaction.annotation.Transactional
    public String clearHistory(Authentication authentication) {
        log.info("User {} is requesting to clear their listening history",
                authentication != null ? authentication.getName() : "anonymous");
        if (authentication == null)
            return "redirect:/login";
        com.revature.revplay.entity.User user = userRepository.findByUsername(authentication.getName()).orElseThrow();
        historyRepository.deleteByUser(user);
        return "redirect:/social/history";
    }

}
