package com.revature.revplay.controller;

import com.revature.revplay.entity.Album;
import com.revature.revplay.entity.Song;
import com.revature.revplay.exception.ResourceNotFoundException;
import com.revature.revplay.repository.AlbumRepository;
import com.revature.revplay.repository.SongRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * This controller specializes in managing the visual experience for music
 * albums.
 * It provides users with a deep dive into an artist's curated collection of
 * tracks,
 * showing both the album's high-level metadata (like cover art and title) and
 * its full track list.
 * By mapping requests under "/album", it organizes the application's view logic
 * around
 * standard musical release structures. It acts as an essential bridge for users
 * transitioning
 * from discovering a single song to exploring a complete musical project.
 */
@Controller
@RequestMapping("/album")
public class AlbumViewController {

    private final AlbumRepository albumRepository;
    private final SongRepository songRepository;

    /**
     * Standard constructor used to wire up essential data repositories for album
     * viewing.
     *
     * The dependencies provided allow the controller to:
     * 1. Retrieve the master album record from the database.
     * 2. Perform a secondary lookup to find every song that belongs to that
     * specific album.
     * 3. Coordinate between the album's meta-data and its constituent tracks.
     * 4. Maintain a high-performance response by querying only the necessary data
     * for the view.
     * 5. This setup ensures that the album detail page is always accurate and fully
     * populated.
     */
    public AlbumViewController(AlbumRepository albumRepository, SongRepository songRepository) {
        this.albumRepository = albumRepository;
        this.songRepository = songRepository;
    }

    /**
     * Renders a detailed information page for a specific music album.
     *
     * The logic for assembling the album detail view includes:
     * 1. Attempting to find the album record by its primary ID or throwing a 404
     * error if missing.
     * 2. Fetching the full list of songs associated with this album's ID from the
     * SongRepository.
     * 3. Attaching both the album metadata and the track list to the 'Model' for
     * the UI.
     * 4. Returning the "album/detail" view which displays the cover art and a
     * playable track list.
     * 5. This method enables a cohesive listening experience for an entire musical
     * release.
     */
    @GetMapping("/{id}")
    public String viewAlbumDetails(@PathVariable("id") Long id, Model model) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Album not found with id: " + id));

        List<Song> songs = songRepository.findByAlbumId(id);

        model.addAttribute("album", album);
        model.addAttribute("songs", songs);

        return "album/detail";
    }
}