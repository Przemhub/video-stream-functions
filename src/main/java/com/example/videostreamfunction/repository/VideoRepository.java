package com.example.videostreamfunction.repository;

import com.example.videostreamfunction.entity.Video;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;


import java.util.UUID;

@Repository
public interface VideoRepository extends JpaRepository<Video, Long> {

}
