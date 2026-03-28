package com.revature.revplay.repository;

import com.revature.revplay.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long> {
        
        Optional<User> findByUsername(String username);

        
        Optional<User> findByUsernameAndEmail(String username, String email);

        
        @org.springframework.data.jpa.repository.Query(value = "SELECT COUNT(*) FROM user_following_artists WHERE artist_id = :artistId", nativeQuery = true)
        long countFollowersByArtistId(@org.springframework.data.repository.query.Param("artistId") Long artistId);

        
        @org.springframework.data.jpa.repository.Query(value = "SELECT u.* FROM users u JOIN user_following_artists ufa ON u.id = ufa.user_id WHERE ufa.artist_id = :artistId", nativeQuery = true)
        List<User> findFollowersByArtistId(@org.springframework.data.repository.query.Param("artistId") Long artistId);

        
        @org.springframework.data.jpa.repository.Query("SELECT u FROM User u LEFT JOIN FETCH u.artistProfile WHERE u.role = :role")
        List<User> findByRole(
                        @org.springframework.data.repository.query.Param("role") com.revature.revplay.entity.Role role);

        
        default List<User> findAllArtists() {
                return findByRole(com.revature.revplay.entity.Role.ARTIST);
        }

        
        @org.springframework.data.jpa.repository.Query(value = "SELECT COUNT(*) FROM user_following_artists WHERE user_id = (SELECT id FROM users WHERE username = :username) AND artist_id = :artistId", nativeQuery = true)
        long countFollowersForUser(@org.springframework.data.repository.query.Param("username") String username,
                        @org.springframework.data.repository.query.Param("artistId") Long artistId);

        Optional<User> findByEmail(String email);

        Optional<User> findByUsernameOrEmail(String username, String email);

        
        boolean existsByUsername(String username);

        boolean existsByEmail(String email);

        
        java.util.List<User> findByDisplayNameContainingIgnoreCaseAndRole(String displayName,
                        com.revature.revplay.entity.Role role);
}
