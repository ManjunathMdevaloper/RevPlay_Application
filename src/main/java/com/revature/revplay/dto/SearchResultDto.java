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


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchResultDto {
    
    private List<Song> songs;
    private List<User> artists;
    private List<Album> albums;
    private List<Playlist> playlists;

    
    public boolean isEmpty() {
        return (songs == null || songs.isEmpty()) &&
                (artists == null || artists.isEmpty()) &&
                (albums == null || albums.isEmpty()) &&
                (playlists == null || playlists.isEmpty());
    }
}
