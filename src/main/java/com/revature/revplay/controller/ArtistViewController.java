import com.revature.revplay.entity.Album;
import com.revature.revplay.entity.ArtistProfile;
import com.revature.revplay.entity.Song;
import com.revature.revplay.entity.User;
import com.revature.revplay.exception.ResourceNotFoundException;
import com.revature.revplay.repository.AlbumRepository;
import com.revature.revplay.repository.ArtistProfileRepository;
import com.revature.revplay.repository.SongRepository;
import com.revature.revplay.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * This controller manages the public-facing identity of music creators on the
 * platform.
 * It provides users with a comprehensive view of an artist, including their
 * discography,
 * social presence, and professional biography. By mapping requests to
 * "/artists", it
 * handles the dynamic routing required to fetch profiles via unique usernames.
 * It acts as the social bridge between listeners and creators, facilitating
 * engagement
 * through follower stats and real-time "Follow" status tracking.
 */
@Controller
@RequestMapping("/artists")
public class ArtistViewController {

    private final UserRepository userRepository;
    private final ArtistProfileRepository artistProfileRepository;
    private final SongRepository songRepository;
    private final AlbumRepository albumRepository;
    private final com.revature.revplay.service.SocialService socialService;

    /**
     * Standard constructor used to initialize the artist discovery system.
     *
     * The dependencies provided here allow the controller to:
     * 1. Validate 'ARTIST' roles and fetch core user accounts.
     * 2. Access detailed artist-only bio and banner information.
     * 3. Pull the full catalog of tracks and albums released by the creator.
     * 4. Interface with social logic for follower counts and community bonding.
     * 5. This setup ensures that public artist pages are content-rich and socially
     * interactive.
     */
    public ArtistViewController(UserRepository userRepository, ArtistProfileRepository artistProfileRepository,
                                SongRepository songRepository, AlbumRepository albumRepository,
                                com.revature.revplay.service.SocialService socialService) {
        this.userRepository = userRepository;
        this.artistProfileRepository = artistProfileRepository;
        this.songRepository = songRepository;
        this.albumRepository = albumRepository;
        this.socialService = socialService;
    }

    /**
     * Renders the complete public profile for a musician based on their custom
     * username.
     *
     * The profile assembly process involves:
     * 1. Verifying that the requested user exists and strictly holds the 'ARTIST'
     * role.
     * 2. Retrieving the specialized ArtistProfile containing their bio and stage
     * name.
     * 3. Querying both the song and album repositories to showcase their collective
     * works.
     * 4. Aggregating social metrics like the current total follower count.
     * 5. Checking the session identity to see if the viewing user is already
     * following this artist.
     * 6. Returning the "artist/public-profile" view for an immersive fan
     * experience.
     */
    @GetMapping("/{username}")
    public String viewArtistPublicProfile(@PathVariable("username") String username,
                                          org.springframework.security.core.Authentication authentication, Model model) {
        User artist = userRepository.findByUsername(username)
                .filter(u -> u.getRole().name().equals("ARTIST"))
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found: " + username));

        ArtistProfile profile = artistProfileRepository.findById(artist.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Artist profile not found: " + username));

        List<Song> songs = songRepository.findByArtist(artist);
        List<Album> albums = albumRepository.findByArtist(artist);

        model.addAttribute("artist", artist);
        model.addAttribute("profile", profile);
        model.addAttribute("songs", songs);
        model.addAttribute("albums", albums);

        model.addAttribute("followerCount", socialService.getFollowerCount(artist.getId()));
        if (authentication != null && authentication.isAuthenticated()) {
            model.addAttribute("isFollowing", socialService.isFollowing(artist.getId(), authentication.getName()));
        }

        return "artist/public-profile";
    }
}