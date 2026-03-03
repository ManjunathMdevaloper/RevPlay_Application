package com.revature.revplay.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * This JPA entity represents a professional musical project (Album) on the
 * platform.
 * An Album serves as a thematic collection of songs released by an artist
 * together.
 * It houses collective branding, such as a high-resolution cover art (BLOB) and
 * descriptive liner notes, providing a formal structure for musical releases.
 * By grouping related tracks, it allows listeners to experience an artist's
 * vision
 * in a more cohesive and professional format than single-track uploads.
 * It acts as the anchor for multi-track storytelling and professional
 * discography.
 */
@Entity
@Table(name = "albums")
@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
// ####################################### Person5 CODE START #########################################
public class Album {

}
// ######################################## Person5 CODE END ##########################################
