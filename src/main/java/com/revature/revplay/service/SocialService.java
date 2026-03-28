package com.revature.revplay.service;

import com.revature.revplay.entity.Song;
import com.revature.revplay.entity.User;

import java.util.List;


public interface SocialService {

    
boolean toggleFollowArtist(Long artistId, String username);

    
    boolean isFollowing(Long artistId, String username);

long getFollowerCount(Long artistId);

    
    List<User> getFollowers(Long artistId);

List<Song> getTopTrendingSongs(int limit);

    
    List<User> getTopArtists(int limit);

long getTotalArtistStreams(Long artistId);
}
