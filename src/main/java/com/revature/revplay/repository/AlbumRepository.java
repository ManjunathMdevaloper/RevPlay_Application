package com.revature.revplay.repository;

import com.revature.revplay.entity.Album;
import com.revature.revplay.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {
    
    List<Album> findByNameContainingIgnoreCase(String name);

    
    List<Album> findByArtist(User artist);
    
    
    @org.springframework.data.jpa.repository.Query("SELECT a FROM Album a WHERE a.songs IS NOT EMPTY")
    List<Album> findAllNonEmpty();

    
    List<Album> findByNameContainingIgnoreCaseAndSongsIsNotEmpty(String name);

    
    List<Album> findByArtistAndSongsIsNotEmpty(User artist);
}
