package com.reactivespring.controller;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.reactivespring.domain.Movie;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.util.Objects;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@AutoConfigureWireMock(port = 8084)
// these will replace the properties' url
@TestPropertySource(properties = {
        "restClient.moviesInfoUrl=http://localhost:8084/v1/movieinfos",
        "restClient.reviewsUrl=http://localhost:8084/v1/reviews"})
public class MoviesControllerIntgTest {

    @Autowired
    WebTestClient webTestClient;


    @Test
    void retrieveMovieById() {

        var movieId = "abc";

        WireMock.stubFor(WireMock.get(WireMock.urlEqualTo("/v1/movieinfos/" + movieId))
                .willReturn(
                        WireMock.aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBodyFile("movieinfo.json")));

        WireMock.stubFor(WireMock.get(WireMock.urlEqualTo("/v1/reviews"))
                .willReturn(
                        WireMock.aResponse()
                                .withHeader("Content-Type", "application/json")
                                .withBodyFile("reviews.json")));


        webTestClient
                .get()
                .uri("/v1/movies/{id}", movieId)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody(Movie.class)
                .consumeWith(movieEntityExchangeResult -> {
                    Movie movie = movieEntityExchangeResult.getResponseBody();
                    assert Objects.requireNonNull(movie).getReviewList().size() == 2;
                });

        WireMock.verify(4, WireMock.getRequestedFor(WireMock.urlEqualTo("/v1/movieinfos/" + movieId)));


    }


}
