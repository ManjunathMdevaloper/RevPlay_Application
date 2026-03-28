package com.revature.revplay.repository;

import com.revature.revplay.entity.Song;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface SongRepository extends JpaRepository<Song, Long> {
        
        List<Song> findByArtist(com.revature.revplay.entity.User artist);

        
        List<Song> findByAlbumId(Long albumId);

        
        @org.springframework.data.jpa.repository.Query("SELECT DISTINCT s.genre FROM Song s WHERE s.genre IS NOT NULL")
        List<String> findAllGenres();

List<Song> findByTitleContainingIgnoreCase(String title);

        @org.springframework.data.jpa.repository.Query("SELECT SUM(s.playCount) FROM Song s WHERE s.artist.id = :artistId")
Long getTotalPlayCountByArtistId(@org.springframework.data.repository.query.Param("artistId") Long artistId);

        @org.springframework.data.jpa.repository.Query("SELECT s FROM Song s ORDER BY s.playCount DESC")
List<Song> findTopTrendingSongs(org.springframework.data.domain.Pageable pageable);

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
