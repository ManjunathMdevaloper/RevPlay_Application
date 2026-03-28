package com.revature.revplay.controller;

import com.revature.revplay.entity.Album;
import com.revature.revplay.entity.Song;
import com.revature.revplay.exception.ResourceNotFoundException;
import com.revature.revplay.repository.AlbumRepository;
import com.revature.revplay.repository.SongRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/album")
@Log4j2
public class AlbumViewController {

    private final AlbumRepository albumRepository;
    private final SongRepository songRepository;

    public AlbumViewController(AlbumRepository albumRepository, SongRepository songRepository) {
        this.albumRepository = albumRepository;
        this.songRepository = songRepository;
    }

    @GetMapping("/{id}")
    public String viewAlbumDetails(@PathVariable("id") Long id, Model model) {
        log.info("Requesting details for album ID: {}", id);
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Album not found with id: " + id));

        List<Song> songs = songRepository.findByAlbumId(id);
        log.debug("Found {} songs for album: {}", songs.size(), album.getName());

        model.addAttribute("album", album);
        model.addAttribute("songs", songs);

        return "album/detail";
    }
}
