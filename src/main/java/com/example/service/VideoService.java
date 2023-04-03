package com.example.service;

import io.micronaut.core.io.ResourceLoader;
import jakarta.inject.Singleton;
import lombok.RequiredArgsConstructor;

import java.io.InputStream;

@RequiredArgsConstructor
@Singleton
public class VideoService {

    private final ResourceLoader resourceLoader;
    private final String MP4_SUFFIX = ".mp4";
//    private final VideoRepository videoRepository;


    public InputStream getVideo(String name) {
        return resourceLoader.getResourceAsStream(String.format("classpath:videos/%s", name.concat(MP4_SUFFIX))).get();
    }

//    public List<Video> getVideoList(){
//        Video video = Video.builder()
//                .videoName("name")
//                .description("descr")
//                .build();
//        videoRepository.save(video);
//        return List.of(videoRepository.findAll().iterator().next());
//    }

}
