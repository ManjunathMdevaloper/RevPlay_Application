package com.revature.revplay.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

/**
 * This JPA entity represents a single "Listening Event" within the platform's
 * analytics engine.
 * Every time a user plays a song, a History record is generated (or updated) to
 * map the
 * specific User to the Song they enjoyed, along with a high-precision
 * timestamp.
 * This data is the foundation for the "Recently Played" UI, personalized music
 * recommendations, and platform-wide trending charts.
 * It provides a transparent audit trail of a user's musical journey on RevPlay.
 */
@Entity
@Table(name = "user_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

// ####################################### Person3 CODE START #########################################
public class History {
}

// ######################################## Person3 CODE END ##########################################
