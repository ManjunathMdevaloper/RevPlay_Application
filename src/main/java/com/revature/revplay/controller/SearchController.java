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


// ####################################### Person2 CODE START #########################################
@Controller
@RequestMapping("/search")
public class SearchController {
}

// ######################################## Person2 CODE END ##########################################
