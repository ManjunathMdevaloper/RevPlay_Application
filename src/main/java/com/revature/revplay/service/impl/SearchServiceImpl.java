package com.revature.revplay.service.impl;

import com.revature.revplay.dto.SearchResultDto;
import com.revature.revplay.entity.Role;
import com.revature.revplay.entity.Song;
import com.revature.revplay.entity.User;
import com.revature.revplay.repository.AlbumRepository;
import com.revature.revplay.repository.PlaylistRepository;
import com.revature.revplay.repository.SongRepository;
import com.revature.revplay.repository.UserRepository;
import com.revature.revplay.service.SearchService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * This class provides the concrete implementation for the platform's music
 * discovery and search logic.
 * It acts as a central aggregator that queries multiple repositories
 * simultaneously to find
 * the most relevant matches for songs, artists, albums, and public playlists.
 * By using case-insensitive searches and complex multi-parameter filtering, it
 * ensures that
 * the user experience remains fast and accurate regardless of library size.
 * It is the core engine behind the "Explore" dashboard and the global search
 * interface.
 */
@Service
public class SearchServiceImpl implements SearchService {

    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final AlbumRepository albumRepository;
    private final PlaylistRepository playlistRepository;

    /**
     * Standard constructor that wires up the repositories needed for multi-category
     * searching.
     * 
     * The variety of repositories allows the search engine to:
     * 1. Access the master song book for track title matches.
     * 2. verify and find user accounts that strictly hold the 'ARTIST' role.
     * 3. Crawl through professional album projects by name.
     * 4. find curated community collections (Playlists) that have been marked as
     * 'Public'.
     * 5. This coordinated setup provides a "Universal Search" experience for the
     * user.
     */
    public SearchServiceImpl(SongRepository songRepository, UserRepository userRepository,
            AlbumRepository albumRepository, PlaylistRepository playlistRepository) {
        this.songRepository = songRepository;
        this.userRepository = userRepository;
        this.albumRepository = albumRepository;
        this.playlistRepository = playlistRepository;
    }

    /**
     * Executes a keyword-based search across all major musical categories.
     * 
     * The comprehensive search workflow includes:
     * 1. sanitizing the input keyword to remove leading or trailing whitespace.
     * 2. performing parallel-ish queries for tracks, artists, albums, and
     * playlists.
     * 3. applying case-insensitive matching to ensure typos or casing don't break
     * searches.
     * 4. strictly filtering for "Public" playlists only to maintain user privacy.
     * 5. returning a unified SearchResultDto that bundles all findings for the UI.
     */
    @Override
    public SearchResultDto searchAll(String keyword) {
        SearchResultDto results = new SearchResultDto();

        if (keyword != null && !keyword.trim().isEmpty()) {
            results.setSongs(songRepository.findByTitleContainingIgnoreCase(keyword.trim()));
            results.setArtists(
                    userRepository.findByDisplayNameContainingIgnoreCaseAndRole(keyword.trim(), Role.ARTIST));
            results.setAlbums(albumRepository.findByNameContainingIgnoreCase(keyword.trim()));
            results.setPlaylists(playlistRepository.findByNameContainingIgnoreCaseAndIsPublicTrue(keyword.trim()));
        }

        return results;
    }

    /**
     * Performs specialized, multi-dimensional filtering for the global song list.
     * 
     * This filtering engine manages:
     * 1. Resolving artist IDs into actual entity objects for deep database joining.
     * 2. formatting partial strings (Title, Genre) to ensure they play well with
     * SQL 'LIKE' clauses.
     * 3. Delegating the complex, conditional query building to the SongRepository.
     * 4. allowing users to drill down by specific genres or artists in the
     * discovery view.
     * 5. This enables high-performance browsing without needing to load every song
     * into memory.
     */
    @Override
    public List<Song> filterSongs(String title, String genre, Long artistId, Long albumId, Integer releaseYear) {
        User artist = null;
        if (artistId != null) {
            artist = userRepository.findById(artistId).orElse(null);
        }

        String formattedTitle = (title != null && !title.isEmpty()) ? title : null;
        String formattedGenre = (genre != null && !genre.isEmpty()) ? genre : null;

        return songRepository.searchAndFilterSongs(formattedTitle, formattedGenre, artist, albumId, releaseYear);
    }

    /**
     * Aggregates and organizes every musical genre currently active on the
     * platform.
     * 
     * The genre gathering logic entails:
     * 1. Querying the entire song library to find all unique, non-null genre tags.
     * 2. filtering out empty strings and ensuring every name is unique (distinct).
     * 3. Sorting the list alphabetically for a professional, easy-to-read UI
     * dropdown.
     * 4. helping the discovery engine stay current with whatever tags artists are
     * using.
     * 5. ensuring that "ghost" genres or deleted styles don't clutter the search
     * filters.
     */
    @Override
    public List<String> getAllGenres() {
        return songRepository.findAllGenres().stream()
                .filter(g -> g != null && !g.isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
}