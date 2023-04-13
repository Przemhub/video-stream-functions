package com.example.controller;

import com.example.dto.VideoPostDTO;
import com.example.entity.Video;
import com.example.service.VideoService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.multipart.CompletedFileUpload;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;


@Controller("/film")
public class VideoController {

    private final VideoService videoService;

    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @Get(value = "/{name}")
    @Produces("video/mp4")
    public InputStream getVideo(@PathVariable String name) {
        return videoService.getVideo(name);

    }
    @Get
    public Iterable<Video> getVideoList(){
        return videoService.getVideoList();
    }
    @Post(consumes = MediaType.MULTIPART_FORM_DATA)
    public HttpResponse<String> uploadVideo(@Part("file") CompletedFileUpload fileUpload, @Body VideoPostDTO videoPostDTO) throws IOException {
        return videoService.postVideo(fileUpload, videoPostDTO);
    }


}
