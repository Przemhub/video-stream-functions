package com.example.videostreamfunction.entity;


import lombok.*;

//import org.springframework.data.relational.core.mapping.Table;


import javax.persistence.*;
import java.util.UUID;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
public class Video {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String videoName;
    private String description;
}
