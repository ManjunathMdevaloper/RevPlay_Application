package com.revature.revplay.controller;

import com.revature.revplay.entity.Song;
import com.revature.revplay.repository.AlbumRepository;
import com.revature.revplay.repository.UserRepository;
import com.revature.revplay.service.PlaylistService;
import com.revature.revplay.service.SearchService;
import com.revature.revplay.service.SongService;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@Controller
@Log4j2
public class DiscoveryController {

    private final SongService songService;
    private final AlbumRepository albumRepository;
    private final UserRepository userRepository;
    private final SearchService searchService;
    private final PlaylistService playlistService;

    public DiscoveryController(SongService songService,
            AlbumRepository albumRepository,
            UserRepository userRepository,
            SearchService searchService,
            PlaylistService playlistService) {
        this.songService = songService;
        this.albumRepository = albumRepository;
        this.userRepository = userRepository;
        this.searchService = searchService;
        this.playlistService = playlistService;
    }

    @GetMapping("/")
    public String home(Model model) {
        log.info("Loading home page (Discovery)");
        model.addAttribute("songs", songService.getAllSongs());
        model.addAttribute("albums", albumRepository.findAllNonEmpty());
        model.addAttribute("artists", userRepository.findAllArtists());
        model.addAttribute("genres", searchService.getAllGenres());
        model.addAttribute("publicPlaylists", playlistService.getAllPublicPlaylists());
        return "discovery/list";
    }

    @GetMapping("/discovery")
    public String discovery(
            @org.springframework.web.bind.annotation.RequestParam(name = "q", required = false) String query,
            Model model) {
        log.info("Accessing discovery view with query: {}", query);
        java.util.List<com.revature.revplay.entity.Playlist> playlists = playlistService.getAllPublicPlaylists();

        if (query != null && !query.trim().isEmpty()) {
            String q = query.toLowerCase();
            playlists = playlists.stream()
                    .filter(p -> p.getName().toLowerCase().contains(q) ||
                            (p.getDescription() != null && p.getDescription().toLowerCase().contains(q)))
                    .collect(java.util.stream.Collectors.toList());
            model.addAttribute("query", query);
        }

        model.addAttribute("publicPlaylists", playlists);
        return "discovery/explore-playlists";
    }

    @GetMapping("/song/{id}")
    public String viewSongDetails(@PathVariable("id") Long id, Model model, Authentication authentication) {
        log.debug("Fetching song details for ID: {}", id);
        Song song = songService.getSongById(id);
        model.addAttribute("song", song);
        if (authentication != null && authentication.isAuthenticated()) {
            log.debug("Authenticated user {} is viewing details for song: {}", authentication.getName(),
                    song.getTitle());
            model.addAttribute("userPlaylists", playlistService.getUserPlaylists(authentication.getName()));
        }
        return "discovery/detail";
    }
}
