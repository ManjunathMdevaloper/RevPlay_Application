package com.revature.revplay.service.impl;

import com.revature.revplay.dto.PlaylistDto;
import com.revature.revplay.entity.Playlist;
import com.revature.revplay.entity.Song;
import com.revature.revplay.entity.User;
import com.revature.revplay.entity.PlaylistFollow;
import com.revature.revplay.exception.ResourceNotFoundException;
import com.revature.revplay.repository.PlaylistRepository;
import com.revature.revplay.repository.SongRepository;
import com.revature.revplay.repository.UserRepository;
import com.revature.revplay.repository.PlaylistFollowRepository;
import com.revature.revplay.service.PlaylistService;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Log4j2
public class PlaylistServiceImpl implements PlaylistService {


private final PlaylistRepository playlistRepository;
private final SongRepository songRepository;
private final UserRepository userRepository;
private final PlaylistFollowRepository playlistFollowRepository;

public PlaylistServiceImpl(PlaylistRepository playlistRepository,
                           SongRepository songRepository,
                           UserRepository userRepository,
                           PlaylistFollowRepository playlistFollowRepository) {
    this.playlistRepository = playlistRepository;
    this.songRepository = songRepository;
    this.userRepository = userRepository;
    this.playlistFollowRepository = playlistFollowRepository;
}

@Override
@Transactional
public Playlist createPlaylist(PlaylistDto playlistDto, String username) {
    log.info("Creating new playlist '{}' for user: {}", playlistDto.getName(), username);

    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    Playlist playlist = Playlist.builder()
            .name(playlistDto.getName())
            .description(playlistDto.getDescription())
            .isPublic(playlistDto.isPublic())
            .user(user)
            .build();

    Playlist saved = playlistRepository.save(playlist);

    log.info("Playlist '{}' created with ID: {}", saved.getName(), saved.getId());

    return saved;
}

@Override
@Transactional(readOnly = true)
public Playlist getPlaylistById(Long id) {

    Playlist playlist = findPlaylistOrThrow(id);

    playlist.getSongs().size();

    return playlist;
}

private Playlist findPlaylistOrThrow(Long id) {

    return playlistRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Playlist not found"));
}

@Override
public List<Playlist> getUserPlaylists(String username) {

    return playlistRepository.findByUser_Username(username);
}

@Override
@Transactional(readOnly = true)
public List<Playlist> getAllPublicPlaylists() {

    List<Playlist> playlists = playlistRepository.findByIsPublicTrue();

    playlists.forEach(p -> p.getSongs().size());

    return playlists;
}

@Override
@Transactional
public Playlist updatePlaylist(Long id, PlaylistDto playlistDto, String username) {

    log.info("Updating playlist ID: {} by user: {}", id, username);

    Playlist playlist = findPlaylistOrThrow(id);

    if (!playlist.getUser().getUsername().equals(username)) {

        log.warn("Unauthorized attempt to update playlist ID: {} by user: {}", id, username);

        throw new RuntimeException("Unauthorized");
    }

    playlist.setName(playlistDto.getName());
    playlist.setDescription(playlistDto.getDescription());
    playlist.setPublic(playlistDto.isPublic());

    Playlist updated = playlistRepository.save(playlist);

    log.info("Playlist ID: {} successfully updated.", id);

    return updated;
}

@Override
@Transactional
public void deletePlaylist(Long id, String username) {

    log.info("Attempting to delete playlist ID: {} by user: {}", id, username);

    Playlist playlist = findPlaylistOrThrow(id);

    if (!playlist.getUser().getUsername().equals(username)) {

        log.warn("Unauthorized attempt to delete playlist ID: {} by user: {}", id, username);

        throw new RuntimeException("Unauthorized");
    }

    playlistRepository.delete(playlist);

    log.info("Playlist ID: {} deleted.", id);
}

@Override
@Transactional
public Playlist addSongToPlaylist(Long playlistId, Long songId, String username) {

    Playlist playlist = findPlaylistOrThrow(playlistId);

    playlist.getSongs().size();

    if (!playlist.getUser().getUsername().equals(username)) {

        throw new RuntimeException("Unauthorized");
    }

    Song song = songRepository.findById(songId)
            .orElseThrow(() -> new ResourceNotFoundException("Song not found"));

    playlist.getSongs().add(song);

    return playlistRepository.save(playlist);
}

@Override
@Transactional
public Playlist removeSongFromPlaylist(Long playlistId, Long songId, String username) {

    Playlist playlist = findPlaylistOrThrow(playlistId);

    playlist.getSongs().size();

    if (!playlist.getUser().getUsername().equals(username)) {

        throw new RuntimeException("Unauthorized");
    }

    Song song = songRepository.findById(songId)
            .orElseThrow(() -> new ResourceNotFoundException("Song not found"));

    playlist.getSongs().remove(song);

    return playlistRepository.save(playlist);
}

@Override
@Transactional
public boolean toggleLikeSong(Long songId, String username) {

    log.info("Toggling like for song ID: {} by user: {}", songId, username);

    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    Song song = songRepository.findById(songId)
            .orElseThrow(() -> new ResourceNotFoundException("Song not found"));

    user.getLikedSongs().size();

    boolean isLiked = user.getLikedSongs().contains(song);

    if (isLiked) {

        user.getLikedSongs().remove(song);

        log.debug("User {} unliked song ID: {}", username, songId);

    } else {

        user.getLikedSongs().add(song);

        log.debug("User {} liked song ID: {}", username, songId);
    }

    userRepository.save(user);

    return !isLiked;
}

@Override
@Transactional(readOnly = true)
public Set<Song> getLikedSongs(String username) {

    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    user.getLikedSongs().size();

    return user.getLikedSongs();
}

@Override
@Transactional(readOnly = true)
public boolean isSongLiked(Long songId, String username) {

    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    Song song = songRepository.findById(songId)
            .orElseThrow(() -> new ResourceNotFoundException("Song not found"));

    user.getLikedSongs().size();

    return user.getLikedSongs().contains(song);
}





@Override
@Transactional
public void followPlaylist(Long playlistId, String username) {

    log.info("User {} attempting to follow playlist {}", username, playlistId);

    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    Playlist playlist = findPlaylistOrThrow(playlistId);

    playlistFollowRepository.findByUserAndPlaylist(user, playlist)
            .ifPresentOrElse(
                    f -> log.info("User already follows playlist"),
                    () -> {

                        PlaylistFollow follow = PlaylistFollow.builder()
                                .user(user)
                                .playlist(playlist)
                                .build();

                        playlistFollowRepository.save(follow);

                        log.info("User {} now follows playlist {}", username, playlistId);
                    });
}

@Override
@Transactional
public void unfollowPlaylist(Long playlistId, String username) {

    log.info("User {} attempting to unfollow playlist {}", username, playlistId);

    User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new ResourceNotFoundException("User not found"));

    Playlist playlist = findPlaylistOrThrow(playlistId);

    playlistFollowRepository.findByUserAndPlaylist(user, playlist)
            .ifPresent(playlistFollowRepository::delete);

    log.info("User {} unfollowed playlist {}", username, playlistId);
}

@Override
@Transactional(readOnly = true)
public long getPlaylistFollowerCount(Long playlistId) {

    Playlist playlist = findPlaylistOrThrow(playlistId);

    return playlistFollowRepository.countByPlaylist(playlist);
}

}

