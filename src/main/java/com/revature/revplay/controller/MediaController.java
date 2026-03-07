package com.revature.revplay.controller;
import com.revature.revplay.entity.Album;
import com.revature.revplay.entity.ArtistProfile;
import com.revature.revplay.entity.Song;
import com.revature.revplay.entity.User;
import com.revature.revplay.repository.AlbumRepository;
import com.revature.revplay.repository.ArtistProfileRepository;
import com.revature.revplay.repository.SongRepository;
import com.revature.revplay.repository.UserRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;

/**
 * This controller is the dedicated engine for serving binary media files across
 * the application.
 * Unlike other controllers that return HTML, this one serves raw bytes for
 * audio and images.
 * It acts as a virtual file server, pulling media directly from the database
 * and delivering
 * it to the browser with the correct 'Content-Type' headers.
 * This is essential for features where media is stored as BLOBs (Binary Large
 * Objects)
 * in the database rather than as simple static files on a path.
 * By centralizing media delivery here, we can enforce security and handle
 * various formats uniformly.
 */
@Controller
@RequestMapping("/api/media")
public class MediaController {

    private final SongRepository songRepository;
    private final AlbumRepository albumRepository;
    private final UserRepository userRepository;
    private final ArtistProfileRepository artistProfileRepository;

    /**
     * Primary constructor to wire up all necessary database repositories.
     * 
     * The injection of these 4 repositories allows the controller to:
     * 1. Access the byte data for songs (MP3s/WAVs).
     * 2. Fetch cover art specifically associated with albums.
     * 3. Handle user-specific profile pictures for personalized dashboards.
     * 4. Retrieve large banner images that define an artist's brand page.
     * 5. Maintain a clean link between the entity IDs in the URL and the data in
     * storage.
     */
    public MediaController(SongRepository songRepository, AlbumRepository albumRepository,
            UserRepository userRepository, ArtistProfileRepository artistProfileRepository) {
        this.songRepository = songRepository;
        this.albumRepository = albumRepository;
        this.userRepository = userRepository;
        this.artistProfileRepository = artistProfileRepository;
    }

    /**
     * Streams the raw audio data for a specific song to the music player.
     * 
     * The detailed logic for audio delivery includes:
     * 1. Receiving a unique song ID from the request URL.
     * 2. Searching the database for the song record and its associated binary data.
     * 3. Checking if the song exists and has valid audio data attached.
     * 4. Setting the 'Content-Type' header so the browser knows if it's an MP3,
     * OGG, or WAV.
     * 5. Wrapping the byte array in a Resource object for efficient web streaming.
     * 6. This method allows the music player to play songs even if they aren't
     * stored as files.
     */
    


    @GetMapping("/song/{id}/audio")
    public ResponseEntity<Resource> getSongAudio(@PathVariable("id") Long id) {
        Song song = songRepository.findById(id).orElse(null);
        if (song == null || song.getAudioData() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE,
                        song.getAudioContentType() != null ? song.getAudioContentType() : "audio/mpeg")
                .body(new ByteArrayResource(song.getAudioData()));
    }

/**
     * Fetches and returns the cover art image for a specific song.
     * 
     * This method ensures the UI looks beautiful by:
     * 1. Retrieving the specific cover image linked to an individual track.
     * 2. Handling the response as a byte array specifically for image rendering.
     * 3. Providing a fallback to 'image/jpeg' if the original format isn't
     * specified.
     * 4. Keeping song-specific art separate from general album-wide art.
     * 5. Ensuring that every song in the playlist has its own visual identity.
     */

@GetMapping("/song/{id}/cover")
    public ResponseEntity<byte[]> getSongCover(@PathVariable("id") Long id) {
        Song song = songRepository.findById(id).orElse(null);
        if (song == null || song.getCoverArtData() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE,
                        song.getCoverArtContentType() != null ? song.getCoverArtContentType() : "image/jpeg")
                .body(song.getCoverArtData());
    }

    /**
     * Serves the main cover image for an entire music album.
     * 
     * The process for delivering album art is:
     * 1. Finding the album in the repository using its primary key (ID).
     * 2. Validating that the album has a valid image blob stored in the database.
     * 3. Constructing an HTTP response with the appropriate image metadata.
     * 4. This art is typically displayed on the Discovery page and Album detail
     * views.
     * 5. It provides a cohesive look for all songs contained within that specific
     * album.
     */
    @GetMapping("/album/{id}/cover")
    public ResponseEntity<byte[]> getAlbumCover(@PathVariable("id") Long id) {
        Album album = albumRepository.findById(id).orElse(null);
        if (album == null || album.getCoverArtData() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE,
                        album.getCoverArtContentType() != null ? album.getCoverArtContentType() : "image/jpeg")
                .body(album.getCoverArtData());
    }

/**
     * Delivers the personal profile picture for a registered RevPlay user.
     * 
     * User profile image delivery is managed by:
     * 1. Fetching the User entity based on the ID requested in the URL.
     * 2. Extracting the profile picture byte array from the user's account data.
     * 3. Ensuring that if a user hasn't uploaded a photo, a 404 is returned
     * (allowing UI fallbacks).
     * 4. This enables user avatars to show up in comments, navbars, and social
     * feeds.
     * 5. It supports personalized branding for every user on the platform.
     */
@GetMapping("/user/{id}/picture")
    public ResponseEntity<byte[]> getUserPicture(@PathVariable("id") Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null || user.getProfilePictureData() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE,
                        user.getProfilePictureContentType() != null ? user.getProfilePictureContentType()
                                : "image/jpeg")
                .body(user.getProfilePictureData());
    }
    /**
     * Retrieves the large background banner for an artist's profile page.
     *
     * Banners are high-impact visual elements handled through:
     * 1. Searching the ArtistProfile records for the matching artist ID.
     * 2. Pulling the high-resolution banner image data stored in the database.
     * 3. Serving the data with high-performance headers like Content-Type.
     * 4. This gives each artist a unique "home base" look on the platform.
     * 5. It automatically scales and delivers the artist's chosen aesthetic to the
     * browser.
     */
    @GetMapping("/artist/{id}/banner")
    public ResponseEntity<byte[]> getArtistBanner(@PathVariable("id") Long id) {
        ArtistProfile profile = artistProfileRepository.findById(id).orElse(null);
        if (profile == null || profile.getBannerImageData() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE,
                        profile.getBannerImageContentType() != null ? profile.getBannerImageContentType()
                                : "image/jpeg")
                .body(profile.getBannerImageData());
    }
}
