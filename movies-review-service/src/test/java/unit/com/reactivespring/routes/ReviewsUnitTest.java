package com.reactivespring.routes;

import com.reactivespring.domain.Review;
import com.reactivespring.handler.ReviewHandler;
import com.reactivespring.repository.ReviewReactiveRepository;
import com.reactivespring.router.ReviewRouter;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@WebFluxTest
@ContextConfiguration(classes = {ReviewRouter.class, ReviewHandler.class})
@AutoConfigureWebTestClient
public class ReviewsUnitTest {

    @MockBean
    private ReviewReactiveRepository reviewReactiveRepository;

    @Autowired
    private WebTestClient webTestClient;

    private static String REVIEW_URL = "/v1/reviews";


    @Test
    void addReview() {
        var review = new Review(null, 1L, "Awesome Movie", 9.0);

        Mockito.when(reviewReactiveRepository.save(ArgumentMatchers.isA(Review.class)))
                .thenReturn(Mono.just(new Review("abc", 1L, "Awesome Movie", 9.0)));

        webTestClient
                .post()
                .uri(REVIEW_URL)
                .bodyValue(review)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(Review.class)
                .consumeWith(reviewEntityExchangeResult -> {
                    Review savedReview = reviewEntityExchangeResult.getResponseBody();
                    assert savedReview != null;
                    assert savedReview.getReviewId() != null;
                });
    }

    @Test
    void getAllReviews() {

        var reviewsList = List.of(
                new Review("1", 1L, "Awesome Movie", 9.0),
                new Review(null, 1L, "Awesome Movie, Great", 9.0),
                new Review(null, 2L, "Excellent Movie", 8.0)
        );

        Mockito.when(reviewReactiveRepository.findAll())
                .thenReturn(Flux.fromIterable(reviewsList));

        webTestClient
                .get()
                .uri(REVIEW_URL)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .hasSize(3);
    }

    @Test
    void updateReview() {
        var review = new Review("1", 1L, "Awesome Movie 2", 9.5);

        Mockito.when(reviewReactiveRepository.findById(review.getReviewId()))
                .thenReturn(Mono.just(new Review("1", 1L, "Awesome Movie", 9.0)));

        Mockito.when(reviewReactiveRepository.save(ArgumentMatchers.isA(Review.class)))
                .thenReturn(Mono.just(review));

        webTestClient
                .put()
                .uri(REVIEW_URL + "/{id}", 1L)
                .bodyValue(review)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(Review.class)
                .consumeWith(reviewEntityExchangeResult -> {
                    Review savedReview = reviewEntityExchangeResult.getResponseBody();
                    assert savedReview.getMovieInfoId().equals(review.getMovieInfoId());
                    assert savedReview.getRating().equals(review.getRating());
                });
    }

    @Test
    void deleteReview() {

        var review = new Review("1", 1L, "Awesome Movie", 9.5);

        Mockito.when(reviewReactiveRepository.findById(review.getReviewId()))
                .thenReturn(Mono.just(review));

        Mockito.when(reviewReactiveRepository.deleteById(review.getReviewId()))
                .thenReturn(Mono.empty());

        webTestClient
                .delete()
                .uri(REVIEW_URL + "/{id}", 1L)
                .exchange()
                .expectStatus()
                .isNoContent();
    }

    @Test
    void getReviewByMovieInfoId() {

        var review = new Review("", 1L, "", 0.0);

        Mockito.when(reviewReactiveRepository.findReviewByMovieInfoId(review.getMovieInfoId()))
                .thenReturn(Flux.just(new Review("abc", 1L, "Awesome Movie", 9.0)));

        webTestClient
                .get()
                .uri(uriBuilder ->
                        uriBuilder.path(REVIEW_URL)
                                .queryParam("movieInfoId", 1L)
                                .build())
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Review.class)
                .consumeWith(listEntityExchangeResult -> {
                    List<Review> responseBody = listEntityExchangeResult.getResponseBody();
                    assert responseBody.size() == 1;
                });
    }


}
