package com.revature.revplay.controller;

import com.revature.revplay.dto.AlbumDto;
import com.revature.revplay.dto.SongDto;
import com.revature.revplay.entity.Album;
import com.revature.revplay.entity.Song;
import com.revature.revplay.entity.User;
import com.revature.revplay.repository.AlbumRepository;
import com.revature.revplay.service.SongService;
import com.revature.revplay.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

/**
 * This controller serves as the central command center for music creators
 * (Artists) on the platform.
 * It provides a private, comprehensive dashboard where artists can manage their
 * musical catalog,
 * analyze their audience metrics, and handle the lifecycle of their tracks and
 * albums.
 * By mapping requests to "/artist/dashboard", it ensures that only verified
 * creators can access
 * these powerful management tools. It integrates deeply with social analytics
 * and media storage
 * to provide a one-stop-shop for artist growth and content distribution.
 */

@Controller
@RequestMapping("/artist/dashboard")
public class ArtistDashboardController {

    private final SongService songService;
    private final UserService userService;
    private final AlbumRepository albumRepository;
    private final com.revature.revplay.service.SocialService socialService;
    private final jakarta.persistence.EntityManager entityManager;
    private final com.revature.revplay.repository.HistoryRepository historyRepository;

    /**
     * Standard constructor that wires up the entire artist-support infrastructure.
     *
     * The dependencies provided here allow the dashboard to:
     * 1. Manage CRUD operations for tracks and professional albums.
     * 2. Calculate real-time analytics like total stream counts and follower
     * growth.
     * 3. Access low-level database tools (EntityManager) for complex cleanup during
     * deletions.
     * 4. Maintain a secure and responsive interface for content creators.
     * 5. This high-level orchestration is what makes the RevPlay artist experience
     * professional-grade.
     */
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

    /**
     * Renders the master dashboard view with full analytics and content management
     * lists.
     *
     * The dashboard rendering process includes:
     * 1. Identifying the authenticated artist and fetching their complete catalog.
     * 2. calculating popularity rankings by sorting songs based on their total play
     * counts.
     * 3. Gathering high-level social metrics like follower counts and listener
     * demographics.
     * 4. Injecting fresh DTOs into the model to power the "Quick Action" song/album
     * forms.
     * 5. returning the "artist/dashboard" view which acts as the mission control
     * for creators.
     */
    @GetMapping
    public String renderDashboard(Authentication authentication, Model model) {
        User artist = userService.getUserByUsername(authentication.getName());
        List<Song> songs = songService.getSongsByArtistId(artist.getId());

        // Songs sorted by play count descending (for streams modal)
        List<Song> songsByStreams = songs.stream()
                .sorted((a, b) -> Long.compare(
                        b.getPlayCount() != null ? b.getPlayCount() : 0L,
                        a.getPlayCount() != null ? a.getPlayCount() : 0L))
                .collect(java.util.stream.Collectors.toList());

        model.addAttribute("songs", songs);
        model.addAttribute("songsByStreams", songsByStreams);
        model.addAttribute("albums", albumRepository.findByArtist(artist));

        // Analytics
        model.addAttribute("totalStreams", socialService.getTotalArtistStreams(artist.getId()));
        model.addAttribute("followerCount", socialService.getFollowerCount(artist.getId()));
        model.addAttribute("followers", socialService.getFollowers(artist.getId()));

        model.addAttribute("songDto", new SongDto());
        model.addAttribute("albumDto", new AlbumDto());

        return "artist/dashboard";
    }

    /**
     * Displays a dedicated standalone form for professional song uploads.
     *
     * The form preparation logic handles:
     * 1. Identifying the artist's library to provide a context for the upload.
     * 2. Pre-fetching a list of the artist's existing albums for categorization.
     * 3. Initializing a clean SongDto to bind the incoming form data.
     * 4. returning the "artist/song-form" view for a focused data entry experience.
     * 5. This ensures that artists have a clear and organized way to introduce new
     * music.
     */
    @GetMapping("/songs/create")
    public String renderCreateSongForm(Authentication authentication, Model model) {
        User artist = userService.getUserByUsername(authentication.getName());

        model.addAttribute("songDto", new SongDto());
        model.addAttribute("albums", albumRepository.findByArtist(artist));
        return "artist/song-form";
    }

    /**
     * Processes the heavy-duty multi-part upload of a new track.
     *
     * This method manages:
     * 1. Capturing the raw audio (MP3/WAV) and optional cover art image bytes.
     * 2. Delegating the complex media storage and database linking to the
     * SongService.
     * 3. Mapping the transactional metadata from the song form to the master
     * database.
     * 4. Providing immediate success feedback through redirect-safe flash
     * attributes.
     * 5. This is the heart of the content distribution pipeline for RevPlay
     * artists.
     */
    @PostMapping("/songs/create")
    public String createSong(Authentication authentication,
                             @ModelAttribute("songDto") SongDto songDto,
                             @RequestParam("audioFile") MultipartFile audioFile,
                             @RequestParam(value = "coverFile", required = false) MultipartFile coverFile,
                             RedirectAttributes redirectAttributes) {

        User artist = userService.getUserByUsername(authentication.getName());
        songService.saveSong(songDto, artist, audioFile, coverFile);

        redirectAttributes.addFlashAttribute("successMessage", "Song uploaded successfully.");
        return "redirect:/artist/dashboard";
    }

    /**
     * Prepares the track editing form with existing metadata and security checks.
     *
     * The edit preparation ensures:
     * 1. Security: It verifies the artist is only editing their own musical
     * property.
     * 2. Accuracy: It hydrates a SongDto with current info (title, genre, album
     * links).
     * 3. Continuity: It allows artists to refine their music without re-uploading
     * the audio file.
     * 4. context: It provides the full list of albums for potential re-assignment.
     * 5. This method helps artists maintain high-quality metadata for their
     * discography.
     */
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

    /**
     * Saves the modified metadata and/or new cover art for an existing track.
     *
     * The update process handles:
     * 1. Validating the artist's ownership of the specific song ID being modified.
     * 2. selectively updating metadata fields through the SongService
     * implementation.
     * 3. Safely replacing the binary cover image if a new one was provided in the
     * form.
     * 4. Returning the artist back to their dashboard with a confirmation message.
     * 5. It ensures that track details remain polished and up-to-date for their
     * audience.
     */
    @PostMapping("/songs/{id}/edit")
    public String editSong(@PathVariable("id") Long id,
                           Authentication authentication,
                           @ModelAttribute("songDto") SongDto songDto,
                           @RequestParam(value = "coverFile", required = false) MultipartFile coverFile,
                           RedirectAttributes redirectAttributes) {

        User artist = userService.getUserByUsername(authentication.getName());
        songService.updateSong(id, songDto, artist.getId(), coverFile);

        redirectAttributes.addFlashAttribute("successMessage", "Song updated successfully.");
        return "redirect:/artist/dashboard";
    }

    /**
     * Triggers a deep-cleaning deletion of a music track.
     *
     * The deletion workflow includes:
     * 1. Verifying the artist's identity to prevent unauthorized media removal.
     * 2. removing all relational links from user libraries, playlists, and history.
     * 3. Terminating the master song record and its binary storage in the database.
     * 4. This ensures that the platform remains free of "orphan" or broken audio
     * links.
     * 5. It is a permanent action that helps artists manage their public presence.
     */
    @PostMapping("/songs/{id}/delete")
    public String deleteSong(@PathVariable("id") Long id, Authentication authentication,
                             RedirectAttributes redirectAttributes) {
        User artist = userService.getUserByUsername(authentication.getName());
        songService.deleteSong(id, artist.getId());

        redirectAttributes.addFlashAttribute("successMessage", "Song deleted completely.");
        return "redirect:/artist/dashboard";
    }

    /**
     * Renders the professional form for creating a new musical project (Album).
     *
     * The preparation logic involves:
     * 1. Initializing an empty AlbumDto to capture the project's branding
     * information.
     * 2. returning the "artist/album-form" template which is tailored for
     * collections.
     * 3. This enables artists to think in terms of curated projects rather than
     * just singles.
     * 4. It acts as the starting point for building a multi-track release on the
     * platform.
     */
    @GetMapping("/albums/create")
    public String renderCreateAlbumForm(Model model) {
        model.addAttribute("albumDto", new AlbumDto());
        return "artist/album-form";
    }

    /**
     * Processes the creation of a new Album entity, including its branding imagery.
     *
     * This method manages:
     * 1. Capturing the professional release metadata (Title, Bio, Release Date).
     * 2. extracting and storing the raw binary bytes for the album's high-res cover
     * art.
     * 3. building the parental relationship between the artist and this new
     * project.
     * 4. Persisting the record so it can immediately start housing redirected
     * tracks.
     * 5. This workflow is essential for building a professional-grade discography.
     */
    @PostMapping("/albums/create")
    public String createAlbum(Authentication authentication,
                              @ModelAttribute("albumDto") AlbumDto albumDto,
                              @RequestParam(value = "coverFile", required = false) MultipartFile coverFile,
                              RedirectAttributes redirectAttributes) {

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
                throw new RuntimeException("Failed to store album cover in database", e);
            }
        }

        albumRepository.save(album);

        redirectAttributes.addFlashAttribute("successMessage", "Album created successfully.");
        return "redirect:/artist/dashboard";
    }

    /**
     * Loads the album modification form with current data and security
     * verification.
     *
     * The edit retrieval process Ensures:
     * 1. The requesting artist is the actual creator of the album project.
     * 2. Current metadata is mapped accurately to the DTO for pre-filling the user
     * interface.
     * 3. The artist can see exactly what their fans see before they commit to
     * changes.
     * 4. This helps maintain consistency across the artist's entire musical
     * portfolio.
     */
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

    /**
     * Saves updated branding and metadata for an existing album project.
     *
     * The update logic handles:
     * 1. Validating that the album belongs to the authenticated artist.
     * 2. Updating high-level fields like project name, description, and release
     * era.
     * 3. Safely replacing the album's visual identity if a new cover image is
     * uploaded.
     * 4. Persistence of these changes across the entire platform's discovery view.
     * 5. This method keeps the artist's professional project views polished and
     * accurate.
     */
    @PostMapping("/albums/{id}/edit")
    public String editAlbum(@PathVariable("id") Long id,
                            Authentication authentication,
                            @ModelAttribute("albumDto") AlbumDto albumDto,
                            @RequestParam(value = "coverFile", required = false) MultipartFile coverFile,
                            RedirectAttributes redirectAttributes) {

        User artist = userService.getUserByUsername(authentication.getName());
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Album not found"));

        if (!album.getArtist().getId().equals(artist.getId())) {
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
                throw new RuntimeException("Failed to update album cover", e);
            }
        }

        albumRepository.save(album);

        redirectAttributes.addFlashAttribute("successMessage", "Album updated successfully.");
        return "redirect:/artist/dashboard";
    }

    /**
     * Performs a complex, deep deletion of an entire album and manages its track
     * orphans.
     *
     * This sophisticated deletion workflow involves:
     * 1. Security: Verifying ownership before any destructive database actions
     * occur.
     * 2. Orphan Management: Looping through all tracks and severing their album
     * links so they don't crash.
     * 3. Relational Cleanup: Manually clearing 'Likes' and 'History' for all songs
     * in the project.
     * 4. Cache Management: Flushing the persistence context to prevent stale state
     * bugs during the multi-step process.
     * 5. Final Execution: Removing the master album record from the database
     * permanently.
     * 6. This ensures a clean slate while keeping the individual songs safely in
     * the general library.
     */
    @PostMapping("/albums/{id}/delete")
    @org.springframework.transaction.annotation.Transactional
    public String deleteAlbum(@PathVariable("id") Long id, Authentication authentication,
                              RedirectAttributes redirectAttributes) {
        User artist = userService.getUserByUsername(authentication.getName());
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Album not found"));

        if (!album.getArtist().getId().equals(artist.getId())) {
            return "redirect:/artist/dashboard";
        }

        // Dissociate songs from album and clear FK references
        List<Song> albumSongs = songService.getSongsByArtistId(artist.getId());
        for (Song song : albumSongs) {
            if (id.equals(song.getAlbumId())) {
                // Clear FK references for this song
                historyRepository.deleteBySong(song);
                entityManager.createNativeQuery("DELETE FROM user_liked_songs WHERE song_id = :songId")
                        .setParameter("songId", song.getId()).executeUpdate();
                entityManager.createNativeQuery("DELETE FROM playlist_songs WHERE song_id = :songId")
                        .setParameter("songId", song.getId()).executeUpdate();
                song.setAlbumId(null);
                songService.saveSong(song);
            }
        }

        entityManager.flush();
        entityManager.clear(); // Clear L1 cache to avoid stale state exceptions
        entityManager.createNativeQuery("DELETE FROM albums WHERE id = :id")
                .setParameter("id", id).executeUpdate();

        redirectAttributes.addFlashAttribute("successMessage", "Album deleted successfully.");
        return "redirect:/artist/dashboard";
    }
}
