package com.revature.revplay.repository;

import com.revature.revplay.entity.PlaylistFollow;
import com.revature.revplay.entity.Playlist;
import com.revature.revplay.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PlaylistFollowRepository extends JpaRepository<PlaylistFollow, Long> {

    Optional<PlaylistFollow> findByUserAndPlaylist(User user, Playlist playlist);

    long countByPlaylist(Playlist playlist);

}