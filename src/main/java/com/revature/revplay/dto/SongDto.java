package com.revature.revplay.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SongDto {
    
    private Long id;
    private String title;

    
    private Long albumId;
    private String genre;
    private Integer duration;
}
