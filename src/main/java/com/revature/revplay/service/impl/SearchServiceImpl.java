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
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Log4j2
public class SearchServiceImpl implements SearchService {

    private final SongRepository songRepository;
    private final UserRepository userRepository;
    private final AlbumRepository albumRepository;
    private final PlaylistRepository playlistRepository;

    
    public SearchServiceImpl(SongRepository songRepository, UserRepository userRepository,
            AlbumRepository albumRepository, PlaylistRepository playlistRepository) {
        this.songRepository = songRepository;
        this.userRepository = userRepository;
        this.albumRepository = albumRepository;
        this.playlistRepository = playlistRepository;
    }

    
    @Override
    public SearchResultDto searchAll(String keyword) {
        log.info("Executing global search for keyword: '{}'", keyword);
        SearchResultDto results = new SearchResultDto();

        if (keyword != null && !keyword.trim().isEmpty()) {
            String trimmed = keyword.trim();
            results.setSongs(songRepository.findByTitleContainingIgnoreCase(trimmed));
            results.setArtists(
                    userRepository.findByDisplayNameContainingIgnoreCaseAndRole(trimmed, Role.ARTIST));
            results.setAlbums(albumRepository.findByNameContainingIgnoreCaseAndSongsIsNotEmpty(trimmed));
            results.setPlaylists(playlistRepository.findByNameContainingIgnoreCaseAndIsPublicTrue(trimmed));

            log.debug("Global search results summary - Songs: {}, Artists: {}, Albums: {}, Playlists: {}",
                    results.getSongs().size(), results.getArtists().size(),
                    results.getAlbums().size(), results.getPlaylists().size());
        }

        return results;
    }

    
    @Override
    public List<Song> filterSongs(String title, String genre, Long artistId, Long albumId, Integer releaseYear) {
        log.info("Applying song filters - Title: {}, Genre: {}, ArtistID: {}, AlbumID: {}, Year: {}",
                title, genre, artistId, albumId, releaseYear);
        User artist = null;
        if (artistId != null) {
            artist = userRepository.findById(artistId).orElse(null);
        }

        String formattedTitle = (title != null && !title.isEmpty()) ? title : null;
        String formattedGenre = (genre != null && !genre.isEmpty()) ? genre : null;

        List<Song> results = songRepository.searchAndFilterSongs(formattedTitle, formattedGenre, artist, albumId,
                releaseYear);
        log.debug("Found {} songs matching filtered criteria", results.size());
        return results;
    }

    
    @Override
    public List<String> getAllGenres() {
        return songRepository.findAllGenres().stream()
                .filter(g -> g != null && !g.isEmpty())
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
}
