
package com.revature.revplay.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDate;

/**
 * This Data Transfer Object (DTO) encapsulates the branding and release
 * information for music albums.
 * It is primarily utilized by artists when creating or updating professional
 * musical projects.
 * By using this object, we can cleanly transmit project titles, descriptions,
 * and release milestones.
 * It serves as the primary data model for the "Create Album" dashboard forms.
 * Using a DTO ensures that the transfer of album metadata is decoupled from the
 * main database entity.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class AlbumDto {
    /**
     * The official title of the musical project as it will appear in searches and
     * on artist pages.
     */
    private String name;

    /**
     * A creative biography or summary providing context for the musical theme of
     * the album.
     */
    private String description;

    /**
     * The chronological milestone representing when this collection was first
     * introduced to the public.
     */
    private LocalDate releaseDate;
}


