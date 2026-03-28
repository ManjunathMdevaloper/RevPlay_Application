package com.revature.revplay.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;


@Entity
@Table(name = "user_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class History {
    
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "history_seq")
    @SequenceGenerator(name = "history_seq", sequenceName = "HISTORY_SEQ", allocationSize = 1)
    private Long id;

    
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    
    @ManyToOne
    @JoinColumn(name = "song_id", nullable = false)
    private Song song;

    
    @Column(name = "played_at")
    private LocalDateTime playedAt;

    
    @PrePersist
    protected void onCreate() {
        playedAt = LocalDateTime.now();
    }
}
