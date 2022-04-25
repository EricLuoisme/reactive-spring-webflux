package com.reactivespring.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ReviewRouter {

    /**
     * It has to be bean to let Spring handle the route
     */
    @Bean
    public RouterFunction<ServerResponse> reviewsRoute() {
        return RouterFunctions.route()
                .GET("/v1/helloworld", request -> ServerResponse.ok().bodyValue("helloworld"))
                .build();
    }
}
