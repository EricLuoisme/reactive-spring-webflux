package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MoviesInfoService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;


@WebFluxTest(controllers = {MoviesInfoController.class})
@AutoConfigureWebTestClient
public class MoviesInfoControllerUnitTest {

    @Autowired
    private WebTestClient webTestClient;

    /**
     * Inject MockBean into Spring Context
     */
    @MockBean
    private MoviesInfoService serviceMock;


    final static String MOVIE_INFO_URL = "/v1/movieinfos";


    @Test
    void getAllMoviesInfo() {

        var movieInfos = List.of(
                new MovieInfo(null, "Batman Begins", 2005, List.of("Christian Bale", "Michael Cane"),
                        LocalDate.parse("2005-06-15")),
                new MovieInfo(null, "The Dark Knight", 2008, List.of("Christian Bale", "Health Ledger"),
                        LocalDate.parse("2008-07-18")),
                new MovieInfo("abc", "Dark Knight Rises", 2012, List.of("Christian Bale", "Tom Hardy"),
                        LocalDate.parse("2012-07-20")));

        Mockito.when(serviceMock.getAllMovieInfos())
                .thenReturn(Flux.fromIterable(movieInfos));

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

        Mockito.when(serviceMock.getMovieInfoById("abc"))
                .thenReturn(Mono.just(new MovieInfo("abc", "Dark Knight Rises", 2012,
                        List.of("Christian Bale", "Tom Hardy"), LocalDate.parse("2012-07-20"))));

        String movieInfoId = "abc";
        webTestClient
                .get()
                .uri(MOVIE_INFO_URL + "/{id}", movieInfoId)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.name")
                .isEqualTo("Dark Knight Rises");
    }

    @Test
    void addMovieInfo() {
        var movieInfo = new MovieInfo(null, "Batman Begins1", 2005,
                List.of("Christain Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        Mockito.when(serviceMock.addMovieInfo(ArgumentMatchers.isA(MovieInfo.class)))
                .thenReturn(Mono.just(
                        new MovieInfo("mockId", "Batman Begins1", 2005,
                                List.of("Christain Bale", "Michael Cane"), LocalDate.parse("2005-06-15")))
                );

        webTestClient
                .post()
                .uri(MOVIE_INFO_URL)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    MovieInfo responseBody = movieInfoEntityExchangeResult.getResponseBody();
                    assert responseBody != null;
                    assert responseBody.getMovieInfoId() != null;
                    Assertions.assertEquals("mockId", responseBody.getMovieInfoId());
                });
    }

    @Test
    void addMovieInfoFail() {
        var movieInfo = new MovieInfo(null, "Batman Begins1", 2005,
                List.of("", "Michael Cane"), LocalDate.parse("2005-06-15"));

        Mockito.when(serviceMock.addMovieInfo(ArgumentMatchers.isA(MovieInfo.class)))
                .thenReturn(Mono.just(
                        new MovieInfo("mockId", "Batman Begins1", 2005,
                                List.of("Christain Bale", "Michael Cane"), LocalDate.parse("2005-06-15")))
                );

        webTestClient
                .post()
                .uri(MOVIE_INFO_URL)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isBadRequest();

    }

    @Test
    void deleteMovieInfoById() {

        Mockito.when(serviceMock.deleteMovieInfo("abc"))
                .thenReturn(Mono.empty());

        String movieInfoId = "abc";
        webTestClient
                .delete()
                .uri(MOVIE_INFO_URL + "/{id}", movieInfoId)
                .exchange()
                .expectStatus()
                .isNoContent();
    }
}
