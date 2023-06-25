package com.example.videostreamfunction.configuration;

import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.function.context.MessageRoutingCallback;
import org.springframework.cloud.function.context.config.RoutingFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.messaging.Message;
import org.springframework.util.ObjectUtils;

import java.util.Objects;
import java.util.function.Consumer;

@Configuration
public class CustomRouterConfiguration {
    private static Logger logger = LoggerFactory.getLogger(CustomRouterConfiguration.class);
    @Bean
    public Consumer<Message<?>> processResponse(final RoutingFunction customRouter) {
        return customRouter::apply;
    }

    @Bean
    public MessageRoutingCallback customRouter() {
        return new MessageRoutingCallback() {
            @SneakyThrows
            @Override
            public String routingResult(Message<?> message) {
                logger.info("Received message from agent: " + message.getHeaders().get("User-Agent"));
                logger.info("headers: " + message.getHeaders());
//                if (!Objects.equals(message.getHeaders().get("Func_Name"), "null")) {
//                    String functionName = ObjectUtils.getDisplayString(message.getHeaders().get("Func_Name"));
//                    logger.info("Request for function: " + functionName);
//                    switch (functionName) {
//                        case "getFilm":
//                        case "postFilm":
//                        case "getFilmList":
//                            return functionName;
//                    }
//                }else{
//                    throw new HttpMessageConversionException("Incorrect Request. Missing func_name header.");
//                }
                return MessageRoutingCallback.super.routingResult(message);
            }
        };
    }
}
