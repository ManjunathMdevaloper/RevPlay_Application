package com.revature.revplay.controller;

import com.revature.revplay.entity.Album;
import com.revature.revplay.entity.ArtistProfile;
import com.revature.revplay.entity.Song;
import com.revature.revplay.entity.User;
import com.revature.revplay.exception.ResourceNotFoundException;
import com.revature.revplay.repository.AlbumRepository;
import com.revature.revplay.repository.ArtistProfileRepository;
import com.revature.revplay.repository.SongRepository;
import com.revature.revplay.repository.UserRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/artists")
@Log4j2
public class ArtistViewController {

    private final UserRepository userRepository;
    private final ArtistProfileRepository artistProfileRepository;
    private final SongRepository songRepository;
    private final AlbumRepository albumRepository;
    private final com.revature.revplay.service.SocialService socialService;

    public ArtistViewController(UserRepository userRepository, ArtistProfileRepository artistProfileRepository,
            SongRepository songRepository, AlbumRepository albumRepository,
            com.revature.revplay.service.SocialService socialService) {
        this.userRepository = userRepository;
        this.artistProfileRepository = artistProfileRepository;
        this.songRepository = songRepository;
        this.albumRepository = albumRepository;
        this.socialService = socialService;
    }

    @GetMapping("/{username}")
    public String viewArtistPublicProfile(@PathVariable("username") String username,
            org.springframework.security.core.Authentication authentication, Model model) {
        log.info("Requesting public profile for artist: {}", username);
        User artist = userRepository.findByUsername(username)
                .filter(u -> u.getRole().name().equals("ARTIST"))
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found: " + username));

        ArtistProfile profile = artistProfileRepository.findById(artist.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Artist profile not found: " + username));

        List<Song> songs = songRepository.findByArtist(artist);
        List<Album> albums = albumRepository.findByArtistAndSongsIsNotEmpty(artist);
        log.debug("Fetched {} songs and {} albums for artist: {}", songs.size(), albums.size(), username);

        model.addAttribute("artist", artist);
        model.addAttribute("profile", profile);
        model.addAttribute("songs", songs);
        model.addAttribute("albums", albums);

        model.addAttribute("followerCount", socialService.getFollowerCount(artist.getId()));
        if (authentication != null && authentication.isAuthenticated()) {
            log.debug("Authenticated viewer {} checking follow status for artist: {}", authentication.getName(),
                    username);
            model.addAttribute("isFollowing", socialService.isFollowing(artist.getId(), authentication.getName()));
        }

        return "artist/public-profile";
    }
}
