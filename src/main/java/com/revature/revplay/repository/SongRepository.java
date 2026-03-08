package com.revature.revplay.repository;

import com.revature.revplay.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * This repository interface manages the database persistence for the platform's
 * music catalog.
 * It contains the complex query logic needed to filter the library by genre,
 * title,
 * artist, and release dates. Additionally, it handles aggregate calculations
 * for
 * streaming analytics, such as totaling play counts for artists and identifying
 * platform-wide trending hits.
 * By utilizing Spring Data JPA's @Query alongside native-like JPQL, it ensures
 * high-performance
 * music discovery even as the song catalog scales.
 */
@Repository
public interface SongRepository extends JpaRepository<Song, Long> {

        /**
         * Pulls all tracks belonging to a specific genre.
         */
List<Song> findByGenre(String genre);

        /**
         * Pulls all tracks belonging to a specific artist.
         */
        List<Song> findByArtist(com.revature.revplay.entity.User artist);

        /**
         * Pulls all tracks belonging to a specific album project.
         */
        List<Song> findByAlbumId(Long albumId);

        /**
         * Pulls all unique genre tags currently assigned to tracks in the database.
         * This data powers the category filters in the "Explore" dashboard, ensuring
         * users see only available musical styles.
         */
        @org.springframework.data.jpa.repository.Query("SELECT DISTINCT s.genre FROM Song s WHERE s.genre IS NOT NULL")
        List<String> findAllGenres();
/**
         * Performs a case-insensitive search for tracks containing the target string in
         * their title.
         */
List<Song> findByTitleContainingIgnoreCase(String title);
/** 
         * Calculates the total lifetime reach of an artist by summing plays across
         * their entire catalog.
         * This single numeric value is the primary "Impact" metric used on professional
         * dashboards.
         */
        @org.springframework.data.jpa.repository.Query("SELECT SUM(s.playCount) FROM Song s WHERE s.artist.id = :artistId")
Long getTotalPlayCountByArtistId(@org.springframework.data.repository.query.Param("artistId") Long artistId);
/**
         * Identifies the current "Hits" on the platform by ranking every track by
         * cumulative plays.
         * This powers the "Top Trending" section of the site, driving passive
         * discovery.
         */
        @org.springframework.data.jpa.repository.Query("SELECT s FROM Song s ORDER BY s.playCount DESC")
List<Song> findTopTrendingSongs(org.springframework.data.domain.Pageable pageable);
/**
         * The platform's most powerful discovery engine—a multi-parameter filtering
         * query.
         * 
         * This sophisticated JPQL logic manages:
         * 1. Boolean 'AND' logic that combines only the filters provided by the user.
         * 2. Null-safety to ensure that empty search fields do not restrict the
         * results.
         * 3. Case-insensitive matching for both titles and genres to improve user UX.
         * 4. extracting release years from timestamps to allow chronological browsing.
         * 5. This method is the heavy-lifter behind the "Advanced Search" and discovery
         * views.
         */
        @org.springframework.data.jpa.repository.Query("SELECT s FROM Song s WHERE " +
                        "(:title IS NULL OR LOWER(s.title) LIKE LOWER(CONCAT('%', :title, '%'))) AND " +
                        "(:genre IS NULL OR LOWER(s.genre) = LOWER(:genre)) AND " +
                        "(:artist IS NULL OR s.artist = :artist) AND " +
                        "(:albumId IS NULL OR s.albumId = :albumId) AND " +
                        "(:releaseYear IS NULL OR EXTRACT(YEAR FROM s.releaseDate) = :releaseYear)")
List<Song> searchAndFilterSongs(
                        @org.springframework.data.repository.query.Param("title") String title,
                        @org.springframework.data.repository.query.Param("genre") String genre,
                        @org.springframework.data.repository.query.Param("artist") com.revature.revplay.entity.User artist,
                        @org.springframework.data.repository.query.Param("albumId") Long albumId,
                        @org.springframework.data.repository.query.Param("releaseYear") Integer releaseYear);
}