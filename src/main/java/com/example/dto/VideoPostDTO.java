package com.example.dto;


public class VideoPostDTO {
    private String videoName;
    private String description;

    public VideoPostDTO(String videoName, String description) {
        this.videoName = videoName;
        this.description = description;
    }

    public VideoPostDTO() {
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
