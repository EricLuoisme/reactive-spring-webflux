package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.repository.MovieInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

// set it into a different port to do the test
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
class MoviesInfoControllerTest {

    @Autowired
    MovieInfoRepository movieInfoRepository;

    @Autowired
    WebTestClient webTestClient;

    static String MOVIE_INFO_URL = "/v1/movieinfos";


    @BeforeEach
    void setUp() {
        var movieInfos = List.of(
                new MovieInfo(null, "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"),
                        LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight", 2008, List.of("Christian Bale", "Health Ledger"),
                        LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises", 2012, List.of("Christian Bale", "Tom Hardy"),
                        LocalDate.parse("2012-07-20")));

        movieInfoRepository.saveAll(movieInfos)
                // we have to block the last, or else it would run concurrently and can not be consumed
                .blockLast();
    }

    @AfterEach
    void tearDown() {
        movieInfoRepository.deleteAll()
                .block();
    }

    @Test
    void addMovieInfo() {
        MovieInfo movieInfo = new MovieInfo(null, "Dark Knight Rises 2222222", 2012,
                List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"));
        webTestClient
                .post()
                .uri(MOVIE_INFO_URL)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(result -> {
                    MovieInfo responseBody = result.getResponseBody();
                    assert responseBody != null;
                    assert responseBody.getMovieInfoId() != null;
                });
    }

    @Test
    void getAllMovieInfos() {
        webTestClient
                .get()
                .uri(MOVIE_INFO_URL)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(3);
    }

    @Test
    void getMovieInfoById() {
        String movieInfoId = "abc";
        webTestClient
                .get()
                .uri(MOVIE_INFO_URL + "/{id}", movieInfoId)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.name").isEqualTo("Dark Knight Rises");
    }

    @Test
    void updateMovieInfoById() {
        String id = "abc";
        MovieInfo movieInfo = new MovieInfo(id, "Dark Knight Rises", 2021,
                List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"));
        webTestClient
                .put()
                .uri(MOVIE_INFO_URL + "/{id}", id)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(body -> {
                    MovieInfo updatedMovieInfo = body.getResponseBody();
                    assert updatedMovieInfo.getYear() == 2021;
                });
    }

    @Test
    void updateMovieInfoById_NotFound() {
        String id = "abccba";
        MovieInfo movieInfo = new MovieInfo(id, "Dark Knight Rises", 2021,
                List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"));
        webTestClient
                .put()
                .uri(MOVIE_INFO_URL + "/{id}", id)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isNotFound();
    }


    @Test
    void deleteMovieInfoById() {
        String movieInfoId = "abc";
        webTestClient
                .delete()
                .uri(MOVIE_INFO_URL + "/{id}", movieInfoId)
                .exchange()
                .expectStatus()
                .isNoContent();
    }
}