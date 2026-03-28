package com.revature.revplay.repository;

import com.revature.revplay.entity.History;
import com.revature.revplay.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface HistoryRepository extends JpaRepository<History, Long> {
    
    List<History> findByUserOrderByPlayedAtDesc(User user);

    
    void deleteByUser(User user);

    
    void deleteBySong(com.revature.revplay.entity.Song song);
}
