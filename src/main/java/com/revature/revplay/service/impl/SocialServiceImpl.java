package com.revature.revplay.service.impl;

import com.revature.revplay.entity.Song;
import com.revature.revplay.entity.User;
import com.revature.revplay.exception.ResourceNotFoundException;
import com.revature.revplay.repository.SongRepository;
import com.revature.revplay.repository.UserRepository;
import com.revature.revplay.service.SocialService;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;


@Service
@Log4j2
public class SocialServiceImpl implements SocialService {

    private final UserRepository userRepository;
    private final SongRepository songRepository;

    
    public SocialServiceImpl(UserRepository userRepository, SongRepository songRepository) {
        this.userRepository = userRepository;
        this.songRepository = songRepository;
    }

    
    @Override
    @Transactional
    public boolean toggleFollowArtist(Long artistId, String username) {
        log.info("User {} is attempting to toggle follow for artist ID: {}", username, artistId);
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        User artist = userRepository.findById(artistId)
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found"));

        if (user.getId().equals(artist.getId())) {
            log.warn("User {} attempted to follow themselves (ID: {})", username, artistId);
            throw new RuntimeException("You cannot follow yourself");
        }

        
        boolean isFollowing = userRepository.countFollowersForUser(username, artistId) > 0;

        if (isFollowing) {
            user.getFollowing().remove(artist);
            userRepository.save(user);
            log.info("User {} unfollowed artist ID: {}", username, artistId);
            return false;
        } else {
            user.getFollowing().add(artist);
            userRepository.save(user);
            log.info("User {} is now following artist ID: {}", username, artistId);
            return true;
        }
    }

    
    @Override
    @Transactional(readOnly = true)
    public boolean isFollowing(Long artistId, String username) {
        if (username == null)
            return false;
        return userRepository.countFollowersForUser(username, artistId) > 0;
    }

    
    @Override
    @Transactional(readOnly = true)
    public long getFollowerCount(Long artistId) {
        return userRepository.countFollowersByArtistId(artistId);
    }

    
    @Override
    @Transactional(readOnly = true)
    public List<User> getFollowers(Long artistId) {
        return userRepository.findFollowersByArtistId(artistId);
    }

    
    @Override
    @Transactional(readOnly = true)
    public List<Song> getTopTrendingSongs(int limit) {
        log.debug("Fetching top {} trending songs", limit);
        return songRepository.findTopTrendingSongs(PageRequest.of(0, limit));
    }

    
    @Override
    @Transactional(readOnly = true)
    public List<User> getTopArtists(int limit) {
        
        
        List<User> artists = userRepository.findAllArtists();
        return artists.stream()
                .sorted((a1, a2) -> {
                    Long s1 = songRepository.getTotalPlayCountByArtistId(a1.getId());
                    Long s2 = songRepository.getTotalPlayCountByArtistId(a2.getId());
                    return Long.compare(s2 != null ? s2 : 0L, s1 != null ? s1 : 0L);
                })
                .limit(limit)
                .collect(Collectors.toList());
    }

    
    @Override
    @Transactional(readOnly = true)
    public long getTotalArtistStreams(Long artistId) {
        Long total = songRepository.getTotalPlayCountByArtistId(artistId);
        return total != null ? total : 0L;
    }
}
