package com.reactivespring.routes;

import com.reactivespring.domain.Review;
import com.reactivespring.repository.ReviewReactiveRepository;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
public class ReviewsIntgTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ReviewReactiveRepository reviewRepository;

    private static String REVIEW_URL = "/v1/reviews";


    @BeforeEach
    void setUp() {
        var reviewsList = List.of(
                new Review("1", 1L, "Awesome Movie", 9.0),
                new Review(null, 1L, "Awesome Movie, Great", 9.0),
                new Review(null, 2L, "Excellent Movie", 8.0)
        );

        reviewRepository.saveAll(reviewsList)
                .blockLast();
    }

    @AfterEach
    void tearDown() {
        reviewRepository.deleteAll()
                .block();
    }

    @Test
    void addReview() {
        var review = new Review(null, 1L, "Awesome Movie", 9.0);

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
        var review = new Review("1", 1L, "Awesome Movie", 9.5);
        webTestClient
                .put()
                .uri(REVIEW_URL + "/{id}", 1)
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
        webTestClient
                .delete()
                .uri(REVIEW_URL + "/{id}", 1L)
                .exchange()
                .expectStatus()
                .isNoContent();
    }

    @Test
    void getReviewByMovieInfoId() {
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
