package com.revature.revplay.controller;

import com.revature.revplay.dto.SearchResultDto;
import com.revature.revplay.entity.Role;
import com.revature.revplay.entity.Song;
import com.revature.revplay.repository.AlbumRepository;
import com.revature.revplay.repository.UserRepository;
import com.revature.revplay.service.SearchService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This controller manages the entire search and music discovery ecosystem of
 * the application.
 * It provides users with powerful tools to find songs, artists, and albums
 * through keyword-based
 * searches and granular filtering. By handling specialized browsing categories
 * like genres,
 * it helps users discover content even when they don't have a specific track in
 * mind.
 * It coordinates with the SearchService to deliver high-performance querying
 * across
 * multiple database tables simultaneously.
 */
@Controller
@RequestMapping("/search")
public class SearchController {

    private final SearchService searchService;
    private final UserRepository userRepository;
    private final AlbumRepository albumRepository;

    /**
     * Standard constructor used to wire up essential repositories and the search
     * service.
     * 
     * The dependencies provided here allow the controller to:
     * 1. Delegate complex cross-entity searching to the SearchService.
     * 2. Quickly fetch a full list of artists from the UserRepository for browsing.
     * 3. Access the entire album catalog via the AlbumRepository.
     * 4. Maintain a clean separation between the user interface and the underlying
     * data store.
     * 5. This setup ensures that discovery features are fast and vertically
     * integrated.
     */
    public SearchController(SearchService searchService, UserRepository userRepository,
            AlbumRepository albumRepository) {
        this.searchService = searchService;
        this.userRepository = userRepository;
        this.albumRepository = albumRepository;
    }

    /**
     * Handles the primary keyword search functionality for the platform.
     * 
     * The logic for this multi-destination search method includes:
     * 1. Sanity checking the input keyword to handle empty or blank searches
     * gracefully.
     * 2. If the query is empty, it redirects the user to the 'Categories' browsing
     * page.
     * 3. If a keyword is provided, it triggers a 'Global Search' that scans songs,
     * artists, and albums.
     * 4. It packages the results into a specialized SearchResultDto for categorized
     * display.
     * 5. Finally, it returns the user to the "search/results" view to see their
     * matches.
     */
    @GetMapping
    public String search(@RequestParam(name = "q", required = false) String keyword, Model model) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return "search/categories";
        }

        SearchResultDto results = searchService.searchAll(keyword);
        model.addAttribute("keyword", keyword);
        model.addAttribute("results", results);

        return "search/results";
    }

    /**
     * Prepares and displays the high-level browsing categories for user discovery.
     * 
     * The browsing preparation involves:
     * 1. Fetching a unique list of all music genres currently present in the
     * system.
     * 2. Retrieving all registered users and filtering them down to those with the
     * 'ARTIST' role.
     * 3. Pulling the complete list of albums to provide a visual catalog for the
     * user.
     * 4. Returning the "search/categories" view which acts as a "Shopping Window"
     * for music.
     * 5. This method is the entry point for users who want to explore without a
     * specific search term.
     */
    @GetMapping("/categories")
    public String browseCategories(Model model) {
        model.addAttribute("genres", searchService.getAllGenres());
        // For Person 2 Categories: Browse by artist, album
        model.addAttribute("artists",
                userRepository.findAll().stream().filter(u -> u.getRole() == Role.ARTIST).collect(Collectors.toList()));
        model.addAttribute("albums", albumRepository.findAll());
        return "search/categories";
    }

    /**
     * Executes highly granular song filtering based on multiple criteria.
     * 
     * The complex filtering logic manages:
     * 1. Aggregating various optional parameters like genre, artist choice, and
     * release year.
     * 2. Calling the search service to perform a multi-predicate query on the song
     * database.
     * 3. Re-injecting all search parameters back into the model to maintain the
     * user's filtered state.
     * 4. Providing the essential navigation info (lists of genres/artists) for
     * refined searching.
     * 5. This allows power users to find exactly the type of sub-genre or era of
     * music they desire.
     */
    @GetMapping("/filter")
    public String filterSongs(
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "genre", required = false) String genre,
            @RequestParam(name = "artistId", required = false) Long artistId,
            @RequestParam(name = "albumId", required = false) Long albumId,
            @RequestParam(name = "releaseYear", required = false) Integer releaseYear,
            Model model) {

        List<Song> songs = searchService.filterSongs(title, genre, artistId, albumId, releaseYear);

        model.addAttribute("songs", songs);
        model.addAttribute("genres", searchService.getAllGenres());
        model.addAttribute("artists",
                userRepository.findAll().stream().filter(u -> u.getRole() == Role.ARTIST).collect(Collectors.toList()));
        model.addAttribute("albums", albumRepository.findAll());

        // Pass back selected values
        model.addAttribute("selectedTitle", title);
        model.addAttribute("selectedGenre", genre);
        model.addAttribute("selectedArtistId", artistId);
        model.addAttribute("selectedAlbumId", albumId);
        model.addAttribute("selectedReleaseYear", releaseYear);

        return "search/filter";
    }
}