package com.example.videostreamfunction.dto;

import lombok.*;

@Data
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class VideoPostDto {
    private String videoName;
    private String description;
}

