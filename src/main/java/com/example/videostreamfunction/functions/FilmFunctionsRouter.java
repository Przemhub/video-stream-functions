package com.example.videostreamfunction.functions;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
@RequiredArgsConstructor
public class FilmFunctionsRouter {

    private final FilmFunctionHandlers handler;

    @Bean
    public RouterFunction<ServerResponse> getFilm() {
        return RouterFunctions.route(RequestPredicates.GET("/film/{videoName:.+}"), handler.filmHandler());
    }

    @Bean
    public RouterFunction<ServerResponse> postFilm() {
        return RouterFunctions.route(RequestPredicates.POST("/film"), handler.postFilmHandler());
    }

    @Bean
    public RouterFunction<ServerResponse> getFilmList() {
        return RouterFunctions.route(RequestPredicates.GET("/film"), handler.filmListHandler());
    }

}
