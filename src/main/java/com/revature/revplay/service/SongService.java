package com.revature.revplay.service;

import com.revature.revplay.entity.Song;

import java.util.List;

/**
 * This interface outlines all music-related business logic and track management
 * operations.
 * It serves as the primary gateway for interacting with individual songs, from
 * basic metadata
 * retrieval to complex file handling and play-tracking analytics.
 * This contract ensures that any implementation provides a consistent way to
 * handle
 * the massive amount of audio data and cover art that flows through the system
 * daily.
 */
public interface SongService {

        /**
         * Retrieves a complete list of every song currently available on the platform.
         * 
         * This method is essential for:
         * 1. Powering the global discovery page where users find new music.
         * 2. Providing a baseline data set for search and filtering features.
         * 3. Allowing administrators to oversee the content library.
         * 4. Ensuring that newest releases can be highlighted to the community.
         */

List<Song> getAllSongs();

        /**
         * Finds a single, specific song record using its unique database ID.
         * 
         * Important facts about this method:
         * 1. It is the primary way the music player fetches metadata for the "Now
         * Playing" track.
         * 2. It connects the ID in the URL to a real song entity with audio and art.
         * 3. It provides the source data for the individual song information pages.
         * 4. It acts as a safety check to ensure a song actually exists before trying
         * to play it.
         */
        Song getSongById(Long id);

/**
         * Fetches a collection of tracks that were created by a specific artist.
         * 
         * This data is vital for:
         * 1. Rendering the 'Artist Profile' pages where fans browse a creator's
         * discography.
         * 2. Powering the 'Artist Dashboard' so musicians can see their own uploaded
         * works.
         * 3. Organizing the library by creator rather than just a flat list of items.
         */
        
// ####################################### Person2 CODE START #########################################

// ######################################## Person2 CODE END ##########################################
// ####################################### Person5 CODE START #########################################

// ######################################## Person5 CODE END ##########################################

// ####################################### Person3 CODE START #########################################

// ######################################## Person3 CODE END ##########################################
}
