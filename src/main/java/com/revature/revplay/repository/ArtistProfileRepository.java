package com.revature.revplay.repository;

import com.revature.revplay.entity.ArtistProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ArtistProfileRepository extends JpaRepository<ArtistProfile, Long> {
}
