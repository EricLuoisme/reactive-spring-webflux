package com.reactivespring.handler;

import com.reactivespring.domain.Review;
import com.reactivespring.exception.ReviewDataException;
import com.reactivespring.repository.ReviewReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.stream.Collectors;

@Slf4j
@Component
public class ReviewHandler {

    @Autowired
    private Validator validator;

    @Autowired
    private ReviewReactiveRepository reviewRepository;


    public Mono<ServerResponse> addReview(ServerRequest request) {
        // adding bean validation inside the traverse
        return request.bodyToMono(Review.class)
                .doOnNext(this::validate)
                .flatMap(reviewRepository::save)
                .flatMap(ServerResponse.status(HttpStatus.CREATED)::bodyValue);
    }

    public Mono<ServerResponse> getReview(ServerRequest request) {
        Flux<Review> fluxResult;
        var movieInfoId = request.queryParam("movieInfoId");
        if (movieInfoId.isPresent()) {
            fluxResult = reviewRepository.findReviewByMovieInfoId(Long.valueOf(movieInfoId.get()));
        } else {
            fluxResult = reviewRepository.findAll();
        }
        return ServerResponse.ok().body(fluxResult, Review.class);
    }

    public Mono<ServerResponse> updateReview(ServerRequest request) {
        // find the existing review record
        Mono<Review> byId = reviewRepository.findById(request.pathVariable("id")).log();
        // copy the new value from the input and save it again
        return byId.flatMap(
                // for the mono request
                review -> request.bodyToMono(Review.class)
                        // 1. map input to the one find from db
                        .map(reqReview -> {
                            review.setComment(reqReview.getComment());
                            review.setRating(reqReview.getRating());
                            return review;
                        })
                        // 2. save into db
                        .flatMap(reviewRepository::save)
                        // 3. for each review we saved, create an server response
                        .flatMap(savedReview -> ServerResponse.ok().bodyValue(savedReview))).log();
    }

    public Mono<ServerResponse> deleteReview(ServerRequest request) {
        Mono<Review> byId = reviewRepository.findById(request.pathVariable("id"));
        return byId.flatMap(review -> reviewRepository.deleteById(review.getReviewId()))
                .then(ServerResponse.noContent().build());
    }

    /**
     * do the bean validation, once find out one of it is invalid, catch the constraint and throw an exception,
     * but the entity before this invalid one, all just go through next step
     *
     * @param review single entity form the request
     */
    private void validate(Review review) {
        var constraintViolations = validator.validate(review);
        log.info("constriant violations:{}", constraintViolations);

        if (constraintViolations.size() > 0) {
            var errorMsg = constraintViolations.stream()
                    .map(ConstraintViolation::getMessage)
                    .sorted()
                    .collect(Collectors.joining(","));
            throw new ReviewDataException(errorMsg);
        }


    }
}
