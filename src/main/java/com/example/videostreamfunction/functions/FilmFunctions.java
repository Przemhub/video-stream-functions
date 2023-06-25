package com.example.videostreamfunction.functions;

import com.example.videostreamfunction.dto.VideoPostDto;
import com.example.videostreamfunction.entity.Video;
import com.example.videostreamfunction.repository.VideoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.functions.HttpRequest;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.codec.multipart.FormFieldPart;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

@Configuration
@RequiredArgsConstructor
public class FilmFunctions {

    private final VideoRepository videoRepository;
    private final Storage storage;
    private static Logger logger = LoggerFactory.getLogger(FilmFunctions.class);
    private final String MP4_SUFFIX = ".mp4";
    private final String videoDirPath = "src/main/resources/videos";
    private static final String GCLOUD_BUCKET = "video-streaming-functions-bucket";


//    @Bean
//    RoutingFunction mySpecialRouter(FunctionCatalog functionCatalog, BeanFactory beanFactory, @Nullable MessageRoutingCallback routingCallback) {
//        Map<String, String> propertiesMap = new HashMap<>();
//        propertiesMap.put(FunctionProperties.PREFIX + ".routing-expression", "'reverse'");
//        return new RoutingFunction(functionCatalog, propertiesMap, new BeanFactoryResolver(beanFactory), routingCallback);
//    }
    @Bean
    public Function<HttpRequest, Mono<ServerResponse>> getFilm() {
        logger.info("getFilm Function");
        return request -> ServerResponse.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(Objects.requireNonNull(filmResource(request.getUri())), Resource.class);
    }

    @Bean
    public Function<HttpRequest, ResponseEntity<Video>> postFilm() {
        logger.info("postFilm Function");
        return request ->
        {
            try {
                Bucket bucket = storage.get(GCLOUD_BUCKET);
                if (bucket == null) {

                    throw new FileNotFoundException("Error in Cloud Storage - Bucket not found");

                }
                Map<String, HttpRequest.HttpPart> parts = request.getParts();
                FilePart videoFilePart = (FilePart) parts.get("video");
                VideoPostDto videoPostDto = extractVideoPostDTO(parts);
                File videoFile = saveVideoFile(videoPostDto, videoFilePart);
                bucket.create(videoPostDto.getVideoName() + MP4_SUFFIX, new FileInputStream(videoFile).readAllBytes());
                Video savedVideo = saveVideoEntity(videoPostDto);
                return ResponseEntity.created(URI.create("/" + savedVideo.getVideoName())).body(savedVideo);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return ResponseEntity.internalServerError().body(null);
        };

    }

    @Bean
    public Function<String, ResponseEntity<List<Video>>> getFilmList() {
        logger.info("getFilmList Function");
        return request -> ResponseEntity.ok()
                .body(videoRepository.findAll());
    }


    private Video saveVideoEntity(VideoPostDto videoPostDto) {
        return videoRepository.save(
                Video.builder()
                        .videoName(videoPostDto.getVideoName())
                        .description(videoPostDto.getDescription())
                        .build()
        );
    }

    private File saveVideoFile(VideoPostDto videoInfo, FilePart videoFilePart) throws IOException {
        Bucket bucket = storage.get(GCLOUD_BUCKET);
        if (bucket == null) {
            throw new FileNotFoundException("Error in Cloud Storage - Bucket not found");
        }
        File videoFile = new File(videoInfo.getVideoName() + MP4_SUFFIX);
        if (videoFile.createNewFile()) {
            videoFilePart.transferTo(videoFile).block();
            return videoFile;
        } else {
            throw new FileNotFoundException("Issue with local video file creation");
        }
    }

    private VideoPostDto extractVideoPostDTO(Map<String, HttpRequest.HttpPart> parts) {
        FormFieldPart videoNamePart = (FormFieldPart) parts.get("videoInfo");
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
