package com.revature.revplay.service;

import com.revature.revplay.dto.SearchResultDto;
import com.revature.revplay.entity.Song;

import java.util.List;


public interface SearchService {

    
    SearchResultDto searchAll(String keyword);

    
    List<Song> filterSongs(String title, String genre, Long artistId, Long albumId, Integer releaseYear);

    
    List<String> getAllGenres();
}
