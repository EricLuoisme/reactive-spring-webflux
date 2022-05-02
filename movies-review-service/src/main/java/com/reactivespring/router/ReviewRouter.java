package com.reactivespring.router;

import com.reactivespring.handler.ReviewHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

@Configuration
public class ReviewRouter {

    /**
     * It has to be bean to let Spring handle the route
     */
    @Bean
    public RouterFunction<ServerResponse> reviewsRoute(ReviewHandler reviewHandler) {
        return RouterFunctions.route()
                .nest(RequestPredicates.path("/v1/reviews"),
                        builder -> builder
                                .POST("", reviewHandler::addReview)
                                .GET("", reviewHandler::getReview)
                                .PUT("/{id}", reviewHandler::updateReview)
                                .DELETE("/{id}", reviewHandler::deleteReview))
                .GET("/v1/helloworld", request -> ServerResponse.ok().bodyValue("helloworld"))
                .build();
    }
}
