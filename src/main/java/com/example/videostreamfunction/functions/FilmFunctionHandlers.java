package com.example.videostreamfunction.functions;

import com.example.videostreamfunction.dto.VideoPostDto;
import com.example.videostreamfunction.entity.Video;
import com.example.videostreamfunction.repository.VideoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.Objects;
import java.util.function.Function;

@RequiredArgsConstructor
@Configuration
public class FilmFunctionHandlers {

    private final VideoRepository videoRepository;
    private final Storage storage;

    private final String MP4_SUFFIX = ".mp4";
    private final String videoDirPath = "src/main/resources/videos";
    private static final String GCLOUD_BUCKET = "video-streaming-functions-bucket";

    public HandlerFunction<ServerResponse> filmHandler() {
        return request -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(Objects.requireNonNull(filmResource(request.pathVariable("videoName"))), Resource.class);
    }

    public HandlerFunction<ServerResponse> filmListHandler() {
        return request -> ServerResponse.ok()
                .bodyValue(videoRepository.findAll());
    }

    public HandlerFunction<ServerResponse> postFilmHandler() {
        return request -> request.multipartData().map(parts -> {

            FilePart videoFilePart = (FilePart) parts.toSingleValueMap().get("video");
            VideoPostDto videoPostDto = extractVideoPostDTO(parts);
            try {
                return saveVideoFile(videoPostDto, videoFilePart)
                        .then(Mono.just(saveVideoEntity(videoPostDto)))
                        .flatMap(savedVideo -> ServerResponse
                                .created(URI.create("/" + savedVideo.getVideoName()))
                                .bodyValue(savedVideo)
                        );

            } catch (IOException e) {
                e.printStackTrace();
                return ServerResponse.badRequest().bodyValue(e.toString());
            }


        }).flatMap(Function.identity());

    }

    private Video saveVideoEntity(VideoPostDto videoPostDto) {
        return videoRepository.save(
                Video.builder()
                        .videoName(videoPostDto.getVideoName())
                        .description(videoPostDto.getDescription())
                        .build()
        );
    }

    private Mono<Void> saveVideoFile(VideoPostDto videoInfo, FilePart videoFilePart) throws IOException {
        Bucket bucket = storage.get(GCLOUD_BUCKET);
        if (bucket == null) {
            throw new FileNotFoundException("Error in Cloud Storage - Bucket not found");
        }
        File videoFile = new File(videoInfo.getVideoName() + MP4_SUFFIX);
        if (videoFile.createNewFile()) {
            return videoFilePart.transferTo(videoFile).doFinally(signalType -> {
                try {
                    Mono.just(bucket.create(videoInfo.getVideoName() + MP4_SUFFIX, new FileInputStream(videoFile).readAllBytes()));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } else {
            throw new FileNotFoundException("Issue with local video file creation");
        }
    }

    private VideoPostDto extractVideoPostDTO(MultiValueMap<String, Part> parts) {
        FormFieldPart videoNamePart = (FormFieldPart) parts.toSingleValueMap().get("videoInfo");
        String videoName = videoNamePart.value();
        try {
            return new ObjectMapper().readValue(videoName, VideoPostDto.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Mono<Resource> filmResource(String videoName) {
        Bucket bucket = storage.get(GCLOUD_BUCKET);
        if (bucket.exists()) {
            Blob blob = bucket.get(videoName + MP4_SUFFIX);
            if (blob.exists()) {
                byte[] content = blob.getContent();
                return Mono.fromSupplier(() -> new ByteArrayResource(content));
            }
        }
        return null;
    }

    private String getPathToFile(String fileName) {
        return String.format("classpath:videos/%s", fileName.concat(MP4_SUFFIX));
    }

}
