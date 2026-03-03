package com.revature.revplay.controller;

import com.revature.revplay.dto.PlaylistDto;
import com.revature.revplay.entity.Playlist;
import com.revature.revplay.entity.Song;
import com.revature.revplay.service.PlaylistService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Set;

/**
 * This controller serves as the primary hub for managing a user's personal
 * music library.
 * It handles the display and organization of liked songs, custom playlists, and
 * track management.
 * By mapping requests under "/library", it provide a centralized location for
 * users to curate
 * their listening experience. The class interacts with the PlaylistService to
 * bridge the
 * gap between the user interface and the underlying database relationships for
 * music curation.
 */

// ####################################### Person4 CODE START #########################################
@Controller
@RequestMapping("/library")
public class LibraryController {

}

// ######################################## Person4 CODE END ##########################################
