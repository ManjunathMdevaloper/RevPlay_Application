package com.revature.revplay.service;

import com.revature.revplay.entity.Song;

import java.util.List;


public interface SongService {

        
List<Song> getAllSongs();
        
        Song getSongById(Long id);

List<Song> getSongsByArtistId(Long artistId);

        Song saveSong(com.revature.revplay.dto.SongDto songDto, com.revature.revplay.entity.User artist,
                        org.springframework.web.multipart.MultipartFile audioFile,
                        org.springframework.web.multipart.MultipartFile coverFile);

        
        Song updateSong(Long id, com.revature.revplay.dto.SongDto songDto, Long artistId,
                        org.springframework.web.multipart.MultipartFile coverFile);

        
        Song saveSong(Song song);

        
        void deleteSong(Long id, Long artistId);

        void recordPlay(Long id, String username);
}

