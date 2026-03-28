package com.revature.revplay.controller;

import com.revature.revplay.dto.SearchResultDto;
import com.revature.revplay.entity.Role;
import com.revature.revplay.entity.Song;
import com.revature.revplay.repository.AlbumRepository;
import com.revature.revplay.repository.UserRepository;
import com.revature.revplay.service.SearchService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.stream.Collectors;


@Controller
@RequestMapping("/search")
@Log4j2
public class SearchController {

    private final SearchService searchService;
    private final UserRepository userRepository;
    private final AlbumRepository albumRepository;

    
    public SearchController(SearchService searchService, UserRepository userRepository,
            AlbumRepository albumRepository) {
        this.searchService = searchService;
        this.userRepository = userRepository;
        this.albumRepository = albumRepository;
    }

    
    @GetMapping
    public String search(@RequestParam(name = "q", required = false) String keyword, Model model) {
        log.info("Global search initiated with keyword: {}", keyword);
        if (keyword == null || keyword.trim().isEmpty()) {
            return "search/categories";
        }

        SearchResultDto results = searchService.searchAll(keyword);
        model.addAttribute("keyword", keyword);
        model.addAttribute("results", results);

        log.debug("Found matches in global search for keyword: {}", keyword);
        return "search/results";
    }

    
    @GetMapping("/categories")
    public String browseCategories(Model model) {
        log.info("Request to browse categories");
        model.addAttribute("genres", searchService.getAllGenres());
        
        model.addAttribute("artists",
                userRepository.findAll().stream().filter(u -> u.getRole() == Role.ARTIST).collect(Collectors.toList()));
        model.addAttribute("albums", albumRepository.findAllNonEmpty());
        return "search/categories";
    }

    
    @GetMapping("/filter")
    public String filterSongs(
            @RequestParam(name = "title", required = false) String title,
            @RequestParam(name = "genre", required = false) String genre,
            @RequestParam(name = "artistId", required = false) Long artistId,
            @RequestParam(name = "albumId", required = false) Long albumId,
            @RequestParam(name = "releaseYear", required = false) Integer releaseYear,
            Model model) {

        log.info("Filtering songs with criteria - Title: {}, Genre: {}, Artist ID: {}, Album ID: {}, Year: {}",
                title, genre, artistId, albumId, releaseYear);
        List<Song> songs = searchService.filterSongs(title, genre, artistId, albumId, releaseYear);

        model.addAttribute("songs", songs);
        model.addAttribute("genres", searchService.getAllGenres());
        model.addAttribute("artists",
                userRepository.findAll().stream().filter(u -> u.getRole() == Role.ARTIST).collect(Collectors.toList()));
        model.addAttribute("albums", albumRepository.findAllNonEmpty());

        
        model.addAttribute("selectedTitle", title);
        model.addAttribute("selectedGenre", genre);
        model.addAttribute("selectedArtistId", artistId);
        model.addAttribute("selectedAlbumId", albumId);
        model.addAttribute("selectedReleaseYear", releaseYear);

        return "search/filter";
    }
}
