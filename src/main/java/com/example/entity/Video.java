package com.example.entity;

import io.micronaut.data.annotation.GeneratedValue;
import io.micronaut.data.annotation.Id;


import javax.persistence.Entity;
import java.util.UUID;

@Entity
public class Video {
    @Id
    @GeneratedValue
    private UUID id;
    private String videoName;
    private String description;

    public Video(UUID id, String videoName, String description) {
        this.id = id;
        this.videoName = videoName;
        this.description = description;
    }

    public Video() {
    }

    public Video(String videoName, String description) {
        this.videoName = videoName;
        this.description = description;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
