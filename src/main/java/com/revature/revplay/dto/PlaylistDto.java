package com.revature.revplay.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlaylistDto {
    
    private Long id;

    
    private String name;

    
    private String description;

    
    private boolean isPublic;
}
