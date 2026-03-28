package com.revature.revplay.controller;

import com.revature.revplay.dto.AlbumDto;
import com.revature.revplay.dto.SongDto;
import com.revature.revplay.entity.Album;
import com.revature.revplay.entity.Song;
import com.revature.revplay.entity.User;
import com.revature.revplay.repository.AlbumRepository;
import com.revature.revplay.service.SongService;
import com.revature.revplay.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/artist/dashboard")
@Log4j2
public class ArtistDashboardController {

    private final SongService songService;
    private final UserService userService;
    private final AlbumRepository albumRepository;
    private final com.revature.revplay.service.SocialService socialService;
    private final jakarta.persistence.EntityManager entityManager;
    private final com.revature.revplay.repository.HistoryRepository historyRepository;

    public ArtistDashboardController(SongService songService, UserService userService,
            AlbumRepository albumRepository,
            com.revature.revplay.service.SocialService socialService,
            jakarta.persistence.EntityManager entityManager,
            com.revature.revplay.repository.HistoryRepository historyRepository) {
        this.songService = songService;
        this.userService = userService;
        this.albumRepository = albumRepository;
        this.socialService = socialService;
        this.entityManager = entityManager;
        this.historyRepository = historyRepository;
    }

    @GetMapping
    public String renderDashboard(Authentication authentication, Model model) {
        log.info("Rendering artist dashboard for user: {}", authentication.getName());
        User artist = userService.getUserByUsername(authentication.getName());
        List<Song> songs = songService.getSongsByArtistId(artist.getId());

        List<Song> songsByStreams = songs.stream()
                .sorted((a, b) -> Long.compare(
                        b.getPlayCount() != null ? b.getPlayCount() : 0L,
                        a.getPlayCount() != null ? a.getPlayCount() : 0L))
                .collect(java.util.stream.Collectors.toList());

        model.addAttribute("songs", songs);
        model.addAttribute("songsByStreams", songsByStreams);
        model.addAttribute("albums", albumRepository.findByArtist(artist));

        model.addAttribute("totalStreams", socialService.getTotalArtistStreams(artist.getId()));
        model.addAttribute("followerCount", socialService.getFollowerCount(artist.getId()));
        model.addAttribute("followers", socialService.getFollowers(artist.getId()));

        model.addAttribute("songDto", new SongDto());
        model.addAttribute("albumDto", new AlbumDto());

        return "artist/dashboard";
    }

    @GetMapping("/songs/create")
    public String renderCreateSongForm(Authentication authentication, Model model) {
        User artist = userService.getUserByUsername(authentication.getName());

        model.addAttribute("songDto", new SongDto());
        model.addAttribute("albums", albumRepository.findByArtist(artist));
        return "artist/song-form";
    }

    @PostMapping("/songs/create")
    public String createSong(Authentication authentication,
            @ModelAttribute("songDto") SongDto songDto,
            @RequestParam("audioFile") MultipartFile audioFile,
            @RequestParam(value = "coverFile", required = false) MultipartFile coverFile,
            RedirectAttributes redirectAttributes) {

        log.info("Processing song upload: '{}' by artist: {}", songDto.getTitle(), authentication.getName());
        User artist = userService.getUserByUsername(authentication.getName());
        songService.saveSong(songDto, artist, audioFile, coverFile);

        log.info("Song '{}' successfully uploaded by {}", songDto.getTitle(), authentication.getName());
        redirectAttributes.addFlashAttribute("successMessage", "Song uploaded successfully.");
        return "redirect:/artist/dashboard";
    }

    @GetMapping("/songs/{id}/edit")
    public String renderEditSongForm(@PathVariable("id") Long id, Authentication authentication, Model model) {
        User artist = userService.getUserByUsername(authentication.getName());
        Song song = songService.getSongById(id);

        if (!song.getArtist().getId().equals(artist.getId())) {
            return "redirect:/artist/dashboard";
        }

        SongDto songDto = SongDto.builder()
                .title(song.getTitle())
                .genre(song.getGenre())
                .albumId(song.getAlbumId())
                .duration(song.getDuration())
                .build();

        model.addAttribute("songDto", songDto);
        model.addAttribute("songId", id);
        model.addAttribute("albums", albumRepository.findByArtist(artist));
        return "artist/song-form";
    }

    @PostMapping("/songs/{id}/edit")
    public String editSong(@PathVariable("id") Long id,
            Authentication authentication,
            @ModelAttribute("songDto") SongDto songDto,
            @RequestParam(value = "coverFile", required = false) MultipartFile coverFile,
            RedirectAttributes redirectAttributes) {

        log.info("Updating song ID: {} for artist: {}", id, authentication.getName());
        User artist = userService.getUserByUsername(authentication.getName());
        songService.updateSong(id, songDto, artist.getId(), coverFile);

        log.info("Song ID: {} successfully updated by {}", id, authentication.getName());
        redirectAttributes.addFlashAttribute("successMessage", "Song updated successfully.");
        return "redirect:/artist/dashboard";
    }

    @PostMapping("/songs/{id}/delete")
    public String deleteSong(@PathVariable("id") Long id, Authentication authentication,
            RedirectAttributes redirectAttributes) {
        log.info("Artist {} is deleting song ID: {}", authentication.getName(), id);
        User artist = userService.getUserByUsername(authentication.getName());
        songService.deleteSong(id, artist.getId());

        log.info("Song ID: {} successfully deleted by {}", id, authentication.getName());
        redirectAttributes.addFlashAttribute("successMessage", "Song deleted completely.");
        return "redirect:/artist/dashboard";
    }

    @GetMapping("/albums/create")
    public String renderCreateAlbumForm(Model model) {
        model.addAttribute("albumDto", new AlbumDto());
        return "artist/album-form";
    }

    @PostMapping("/albums/create")
    public String createAlbum(Authentication authentication,
            @ModelAttribute("albumDto") AlbumDto albumDto,
            @RequestParam(value = "coverFile", required = false) MultipartFile coverFile,
            RedirectAttributes redirectAttributes) {

        log.info("Artist {} is creating album: '{}'", authentication.getName(), albumDto.getName());
        User artist = userService.getUserByUsername(authentication.getName());

        Album album = Album.builder()
                .name(albumDto.getName())
                .description(albumDto.getDescription())
                .releaseDate(albumDto.getReleaseDate())
                .artist(artist)
                .build();

        if (coverFile != null && !coverFile.isEmpty()) {
            try {
                album.setCoverArtData(coverFile.getBytes());
                album.setCoverArtContentType(coverFile.getContentType());
            } catch (java.io.IOException e) {
                log.error("Failed to byte-read album cover for project: {}", albumDto.getName(), e);
                throw new RuntimeException("Failed to store album cover in database", e);
            }
        }

        Album saved = albumRepository.save(album);
        log.info("Album '{}' created with ID: {}", saved.getName(), saved.getId());

        redirectAttributes.addFlashAttribute("successMessage", "Album created successfully.");
        return "redirect:/artist/dashboard";
    }

    @GetMapping("/albums/{id}/edit")
    public String renderEditAlbumForm(@PathVariable("id") Long id, Authentication authentication, Model model) {
        User artist = userService.getUserByUsername(authentication.getName());
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Album not found"));

        if (!album.getArtist().getId().equals(artist.getId())) {
            return "redirect:/artist/dashboard";
        }

        AlbumDto albumDto = AlbumDto.builder()
                .name(album.getName())
                .description(album.getDescription())
                .releaseDate(album.getReleaseDate())
                .build();

        model.addAttribute("albumDto", albumDto);
        model.addAttribute("albumId", id);
        return "artist/album-form";
    }

    @PostMapping("/albums/{id}/edit")
    public String editAlbum(@PathVariable("id") Long id,
            Authentication authentication,
            @ModelAttribute("albumDto") AlbumDto albumDto,
            @RequestParam(value = "coverFile", required = false) MultipartFile coverFile,
            RedirectAttributes redirectAttributes) {

        log.info("Updating album ID: {} for artist: {}", id, authentication.getName());
        User artist = userService.getUserByUsername(authentication.getName());
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Album not found"));

        if (!album.getArtist().getId().equals(artist.getId())) {
            log.warn("Unauthorized modification attempt for album ID: {} by artist: {}", id, authentication.getName());
            return "redirect:/artist/dashboard";
        }

        album.setName(albumDto.getName());
        album.setDescription(albumDto.getDescription());
        album.setReleaseDate(albumDto.getReleaseDate());

        if (coverFile != null && !coverFile.isEmpty()) {
            try {
                album.setCoverArtData(coverFile.getBytes());
                album.setCoverArtContentType(coverFile.getContentType());
            } catch (java.io.IOException e) {
                log.error("Failed to update album cover for project ID {}: ", id, e);
                throw new RuntimeException("Failed to update album cover", e);
            }
        }

        albumRepository.save(album);
        log.info("Album ID: {} successfully updated by {}", id, authentication.getName());

        redirectAttributes.addFlashAttribute("successMessage", "Album updated successfully.");
        return "redirect:/artist/dashboard";
    }

    @PostMapping("/albums/{id}/delete")
    @org.springframework.transaction.annotation.Transactional
    public String deleteAlbum(@PathVariable("id") Long id, Authentication authentication,
            RedirectAttributes redirectAttributes) {
        log.info("Artist {} is deleting album ID: {}", authentication.getName(), id);
        User artist = userService.getUserByUsername(authentication.getName());
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Album not found"));

        if (!album.getArtist().getId().equals(artist.getId())) {
            log.warn("Unauthorized delete attempt for album ID: {} by artist: {}", id, authentication.getName());
            return "redirect:/artist/dashboard";
        }

        
        List<Song> albumSongs = songService.getSongsByArtistId(artist.getId());
        for (Song song : albumSongs) {
            if (id.equals(song.getAlbumId())) {
                
                historyRepository.deleteBySong(song);
                entityManager.createNativeQuery("DELETE FROM user_liked_songs WHERE song_id = :songId")
                        .setParameter("songId", song.getId()).executeUpdate();
                entityManager.createNativeQuery("DELETE FROM playlist_songs WHERE song_id = :songId")
                        .setParameter("songId", song.getId()).executeUpdate();
                song.setAlbumId(null);
                songService.saveSong(song);
                log.debug("Orphaned song ID: {} from deleted album project", song.getId());
            }
        }

        entityManager.flush();
        entityManager.clear(); 
        entityManager.createNativeQuery("DELETE FROM albums WHERE id = :id")
                .setParameter("id", id).executeUpdate();

        log.info("Album ID: {} successfully purged from system by {}", id, authentication.getName());
        redirectAttributes.addFlashAttribute("successMessage", "Album deleted successfully.");
        return "redirect:/artist/dashboard";
    }
}
