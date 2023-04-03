package com.example.controller;

import com.example.service.VideoService;
import io.micronaut.core.io.ResourceLoader;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.PathVariable;

import java.io.InputStream;


@Controller("/film")
public class VideoController {

    private final VideoService videoService;


    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }


    @Get(value = "/{name}", produces = "video/mp4")
    public InputStream getVideo(@PathVariable(defaultValue = "video") String name) {
        return videoService.getVideo(name);

    }


}
