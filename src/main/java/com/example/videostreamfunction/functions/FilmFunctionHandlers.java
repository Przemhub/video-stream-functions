package com.example.videostreamfunction.functions;

import com.example.videostreamfunction.dto.VideoPostDto;
import com.example.videostreamfunction.entity.Video;
import com.example.videostreamfunction.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.HandlerFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

@RequiredArgsConstructor
@Configuration
public class FilmFunctionHandlers {

    private final VideoRepository videoRepository;
    private final ResourceLoader resourceLoader;

    public HandlerFunction<ServerResponse> filmHandler() {
        return request -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(filmResource(), Resource.class);

    }

    public HandlerFunction<ServerResponse> postFilmHandler() {
        return request -> {
            VideoPostDto videoPostDto = request.body(BodyExtractors.toMono(VideoPostDto.class)).block();
            assert videoPostDto != null;
            Video video = Video.builder()
                    .description(videoPostDto.getDescription())
                    .videoName(videoPostDto.getVideoName())
                    .build();
            Video savedVideo = videoRepository.save(video);

            return ServerResponse.created(URI.create("/" + savedVideo.getVideoName()))
                    .bodyValue(savedVideo);
        };

    }
    public HandlerFunction<ServerResponse> filmListHandler() {
        return request -> {
           return ServerResponse.ok()
                    .bodyValue(videoRepository.findAll());
        };
    }
    public Mono<Resource> filmResource() {
        return Mono.fromSupplier(() -> resourceLoader.getResource("classpath:videos/video.mp4"));
    }

}
