package com.example.videostreamfunction.functions;

import com.example.videostreamfunction.dto.VideoPostDto;
import com.example.videostreamfunction.entity.Video;
import com.example.videostreamfunction.repository.VideoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyExtractors;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.net.URI;

@Configuration
@RequiredArgsConstructor
public class FilmFunctionsRouter {

//    @Autowired
    private final FilmFunctionHandlers handler;
    private final ResourceLoader resourceLoader;
    private final VideoRepository videoRepository;




    @Bean
    public RouterFunction<ServerResponse> getFilm() {
        return RouterFunctions.route(RequestPredicates.GET("/film/.*"), handler.filmHandler());
    }

    @Bean
    public RouterFunction<ServerResponse> postFilm() {
        return RouterFunctions.route(RequestPredicates.POST("/film"), handler.postFilmHandler());
    }

    @Bean
    public RouterFunction<ServerResponse> getFilmList() {
        return RouterFunctions.route(RequestPredicates.GET("/film"), handler.filmListHandler());
    }



//    @Override
//    public void service(HttpRequest httpRequest, HttpResponse httpResponse) throws Exception {
//
//
//            }
//        }
//    }
}
