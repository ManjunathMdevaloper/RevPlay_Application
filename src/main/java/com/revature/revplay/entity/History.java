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
public class History {
    /**
     * The unique database primary key for the history event, managed via sequence.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "history_seq")
    @SequenceGenerator(name = "history_seq", sequenceName = "HISTORY_SEQ", allocationSize = 1)
    private Long id;

    /**
     * The listener who initiated the playback session.
     */
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * The specific musical track that was engaged with by the user.
     */
    @ManyToOne
    @JoinColumn(name = "song_id", nullable = false)
    private Song song;

    /**
     * The precise calendar date and time when the song playback occurred.
     */
    @Column(name = "played_at")
    private LocalDateTime playedAt;

    /**
     * Lifecycle hook that automatically captures the playback time before the
     * record is saved.
     */
    @PrePersist
    protected void onCreate() {
        playedAt = LocalDateTime.now();
    }
}