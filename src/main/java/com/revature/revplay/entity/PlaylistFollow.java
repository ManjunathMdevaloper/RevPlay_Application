package com.revature.revplay.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "playlist_follows")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaylistFollow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name="playlist_id")
    private Playlist playlist;
}

