package com.revature.revplay.dto;

import com.revature.revplay.entity.Album;
import com.revature.revplay.entity.Playlist;
import com.revature.revplay.entity.Song;
import com.revature.revplay.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * This Data Transfer Object (DTO) serves as a unified container for many types
 * of search results.
 * When a user performs a search, the system may find matching songs, artists,
 * albums, or playlists.
 * Instead of returning these separately, this object bundles all related
 * findings into a single delivery.
 * This allows the front-end to render a categorized and comprehensive view of
 * all system matches.
 * It is the core data structure used by the SearchService to relay findings
 * back to the UI.
 * This object is fundamental to the platform's ability to provide a
 * "Google-like" discovery experience.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder


// ####################################### Person2 CODE START #########################################
public class SearchResultDto {

}
// ######################################## Person2 CODE END ##########################################
