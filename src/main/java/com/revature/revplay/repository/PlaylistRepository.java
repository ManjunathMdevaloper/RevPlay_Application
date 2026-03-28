package com.revature.revplay.repository;

import com.revature.revplay.entity.Playlist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    
    List<Playlist> findByNameContainingIgnoreCaseAndIsPublicTrue(String name);

    
    List<Playlist> findByUser(com.revature.revplay.entity.User user);

    
    List<Playlist> findByUser_Username(String username);

    
    List<Playlist> findByIsPublicTrue();
}
