package com.example.videostreamfunction.functions;

import com.example.videostreamfunction.dto.VideoPostDto;
import com.example.videostreamfunction.entity.Video;
import com.example.videostreamfunction.repository.VideoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.http.codec.multipart.Part;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.util.function.Function;

@RequiredArgsConstructor
@Configuration
public class FilmFunctionHandlers {

    private final VideoRepository videoRepository;
    private final ResourceLoader resourceLoader;
    private final String MP4_SUFFIX = ".mp4";
    private final String videoDirPath = "src/main/resources/videos";

    public HandlerFunction<ServerResponse> filmHandler() {
        return request -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(filmResource(request.pathVariable("videoName")), Resource.class);

    }

    public HandlerFunction<ServerResponse> postFilmHandler() {
        return request -> request.multipartData().map(parts -> {
            FilePart videoFilePart = (FilePart) parts.toSingleValueMap().get("file");

            VideoPostDto videoPostDto = extractVideoPostDTO(parts);
            saveVideoFile(videoPostDto.getVideoName(), videoFilePart);
            Video savedVideo = videoRepository.save(
                    Video.builder()
                            .videoName(videoPostDto.getVideoName())
                            .description(videoPostDto.getDescription())
                            .build()
            );
            return ServerResponse.created(URI.create("/" + savedVideo.getVideoName()))
                    .bodyValue(savedVideo);
        }).flatMap(Function.identity());

    }

    private void saveVideoFile(String filename, FilePart videoFilePart) {
        File dest = new File(videoDirPath, filename);
        videoFilePart.content().map(fileUpload -> {
            try {
                return Files.copy(fileUpload.asInputStream(), dest.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        });
    }

    private VideoPostDto extractVideoPostDTO(MultiValueMap<String, Part> parts) {
        FormFieldPart videoNamePart = (FormFieldPart) parts.toSingleValueMap().get("videoPostDto");
        String videoName = videoNamePart.value();
        try {
            return new ObjectMapper().readValue(videoName, VideoPostDto.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public HandlerFunction<ServerResponse> filmListHandler() {
        return request -> ServerResponse.ok()
                .bodyValue(videoRepository.findAll());
    }

    private Mono<Resource> filmResource(String videoName) {
        return Mono.fromSupplier(() -> resourceLoader.getResource(getPathToFile(videoName)));
    }

    private String getPathToFile(String fileName) {
        return String.format("classpath:videos/%s", fileName.concat(MP4_SUFFIX));
    }

}
