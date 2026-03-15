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

/**
 * This controller is the main entry point for the application's user
 * experience, handling the home page
 * and community discovery features. It aggregates data from across the
 * system—songs, albums,
 * artists, and public playlists—to provide a rich, immersive dashboard for
 * every user.
 * By mapping both the root context "/" and "/discovery", it ensures that users
 * are met with
 * trending and curated content immediately upon arrival.
 * It serves as a visual gateway that encourages exploration and social
 * interaction on the platform.
 */
@Controller
@Log4j2
public class DiscoveryController {

    private final SongService songService;
    private final AlbumRepository albumRepository;
    private final UserRepository userRepository;
    private final SearchService searchService;
    private final PlaylistService playlistService;

    /**
     * Comprehensive constructor that wires up five different data providers.
     * 
     * The variety of dependencies allows this controller to:
     * 1. Collate songs, albums, and artist profiles into a single unified view.
     * 2. Provide a snapshot of the community's public playlists for new discovery.
     * 3. Access genre categories to help users filter their starting experience.
     * 4. Maintain a high-performance home feed through direct service/repository
     * access.
     * 5. This multifaceted setup is what makes the RevPlay home page feel alive and
     * dynamic.
     */
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

    /**
     * Renders the primary landing page (Home) for both guests and logged-in users.
     * 
     * The dashboard assembly process includes:
     * 1. fetching every song currently available to populate the global library
     * view.
     * 2. Gathering all albums and verified artists to showcase the platform's
     * professional side.
     * 3. Loading community-curated public playlists to give users a variety of
     * starting points.
     * 4. Categorizing content by genre to facilitate easy navigation from the jump.
     * 5. Returning the "discovery/list" view, which is the "front door" of the
     * RevPlay experience.
     */
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

    /**
     * Manages a specialized 'Explore' view focused strictly on public user
     * playlists.
     * 
     * This social discovery logic performs:
     * 1. retrieving the full list of playlists that users have chosen to share with
     * the platform.
     * 2. optionally filtering that list by a search query to find specific themes
     * or creators.
     * 3. Handling the case-insensitive search through names and descriptions to
     * ensure accuracy.
     * 4. Returning only the relevant matches to populate the "explore-playlists"
     * gallery.
     * 5. It encourages users to see what their peers are listening to and follow
     * new tastes.
     */
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

    /**
     * Provides a focused view for an individual song, including metadata and
     * library options.
     * 
     * The detail view logic encompasses:
     * 1. Fetching the specific song entity and all its associated artist/album
     * data.
     * 2. Checking the authentication status to provide user-specific "Add to
     * Playlist" options.
     * 3. Pre-loading the user's private playlists so they can quickly curate what
     * they hear.
     * 4. returning the "discovery/detail" view, which acts as the information hub
     * for a track.
     * 5. Ensuring that both the music player and the text descriptions are
     * synchronized for the user.
     */
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