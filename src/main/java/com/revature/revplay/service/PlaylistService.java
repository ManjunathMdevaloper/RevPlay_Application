package com.revature.revplay.service;

import com.revature.revplay.dto.PlaylistDto;
import com.revature.revplay.entity.Playlist;
import com.revature.revplay.entity.Song;

import java.util.List;
import java.util.Set;


public interface PlaylistService {

    
    Playlist createPlaylist(PlaylistDto playlistDto, String username);

    
    Playlist getPlaylistById(Long id);

    
    List<Playlist> getUserPlaylists(String username);

    
    List<Playlist> getAllPublicPlaylists();

    
    Playlist updatePlaylist(Long id, PlaylistDto playlistDto, String username);

    
    void deletePlaylist(Long id, String username);

    
    Playlist addSongToPlaylist(Long playlistId, Long songId, String username);

    
    Playlist removeSongFromPlaylist(Long playlistId, Long songId, String username);

    
    boolean toggleLikeSong(Long songId, String username);

    
    Set<Song> getLikedSongs(String username);

    
    boolean isSongLiked(Long songId, String username);
    
    void followPlaylist(Long playlistId, String username);

    void unfollowPlaylist(Long playlistId, String username);
    
    long getPlaylistFollowerCount(Long playlistId);
}
