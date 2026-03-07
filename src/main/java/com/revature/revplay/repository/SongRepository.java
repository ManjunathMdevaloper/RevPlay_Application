package com.revature.revplay.repository;

import com.revature.revplay.entity.Song;
import com.revature.revplay.entity.User;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

@Repository
public interface SongRepository extends JpaRepository<Song, Long> {

    /**
     * Pulls all tracks belonging to a specific genre.
     */
    List<Song> findByGenreIgnoreCase(String genre);

    /**
     * Performs a case-insensitive search for tracks containing
     * the target string in their title.
     */
    List<Song> findByTitleContainingIgnoreCase(String title);

    List<Song> findByArtist(User artist);

    List<Song> findByAlbumId(Long albumId);

    /**
     * Identifies the current "Hits" on the platform by ranking
     * every track by cumulative plays.
     */
    @Query("SELECT s FROM Song s ORDER BY s.playCount DESC")
    List<Song> findTopTrendingSongs(Pageable pageable);

    /**
     * Advanced search filtering query.
     */
    @Query("SELECT s FROM Song s WHERE " +
            "(:title IS NULL OR LOWER(s.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
            "(:genre IS NULL OR LOWER(s.genre) = LOWER(:genre)) AND " +
            "(:artistId IS NULL OR s.artist.id = :artistId) AND " +
            "(:albumId IS NULL OR s.albumId = :albumId) AND " +
            "(:releaseYear IS NULL OR EXTRACT(YEAR FROM s.releaseDate) = :releaseYear)")
    List<Song> filterSongs(
            @Param("title") String title,
            @Param("genre") String genre,
            @Param("artistId") Long artistId,
            @Param("albumId") Long albumId,
            @Param("releaseYear") Integer releaseYear
    );
}