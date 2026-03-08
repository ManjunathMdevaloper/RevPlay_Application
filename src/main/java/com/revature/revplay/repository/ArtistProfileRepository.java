
package com.revature.revplay.repository;

import com.revature.revplay.entity.ArtistProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * This repository interface manages the professional profile metadata for
 * creators.
 * It provides the primary data access layer for artist branding, including
 * professional stage names, biographies, banner images, and social media
 * footprints.
 * By using a One-to-One shared primary key with the User entity, it ensures
 * that professional identities are securely anchored to verified accounts.
 * This is the core persistence layer for the "Artist Profile" and creation
 * tools.
 */
@Repository
public interface ArtistProfileRepository extends JpaRepository<ArtistProfile, Long> {
}