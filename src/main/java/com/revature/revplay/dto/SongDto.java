package com.revature.revplay.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * This Data Transfer Object (DTO) represents the essential metadata for a
 * single music track.
 * It is used primarily during the song upload and editing phases of the
 * artist's workflow.
 * By using this object, we can bundle title, genre, and duration into a single
 * package for easy transmission.
 * It acts as the blueprint for creating or updating a permanent 'Song' entity
 * in the database.
 * This decoupled approach allows us to validate track information before
 * processing heavy audio data.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

// ####################################### Person5 CODE START #########################################
public class SongDto {

}

// ######################################## Person5 CODE END ##########################################
