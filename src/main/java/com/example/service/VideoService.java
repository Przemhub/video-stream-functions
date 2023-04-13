package com.example.service;

import com.example.dto.VideoPostDTO;
import com.example.entity.Video;
import com.example.repository.VideoRepository;
import io.micronaut.core.io.ResourceLoader;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.multipart.CompletedFileUpload;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;


@Singleton
public class VideoService {
    private final String videoDirPath = "src/main/resources/videos";
    private final ResourceLoader resourceLoader;
    private final String MP4_SUFFIX = ".mp4";
    private final VideoRepository videoRepository;

    public VideoService(ResourceLoader resourceLoader, VideoRepository videoRepository) {
        this.resourceLoader = resourceLoader;
        this.videoRepository = videoRepository;
    }


    public InputStream getVideo(String name) {
        return resourceLoader.getResourceAsStream(String.format("classpath:videos/%s", name.concat(MP4_SUFFIX))).get();
    }

    public HttpResponse<String> postVideo(CompletedFileUpload fileUpload, VideoPostDTO videoPostDTO) {
        File videoDir = new File(videoDirPath);
        if (!videoDir.exists()) {
            videoDir.mkdirs();
        }
        String filename = fileUpload.getFilename();
        File dest = new File(videoDir, filename);

        try {
            Files.copy(fileUpload.getInputStream(), dest.toPath());
            Video video = new Video(videoPostDTO.getVideoName(), videoPostDTO.getDescription());
            videoRepository.save(video);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return HttpResponse.ok("File uploaded successfully");
    }

    public Iterable<Video> getVideoList() {

        return videoRepository.findAll();
    }


}
