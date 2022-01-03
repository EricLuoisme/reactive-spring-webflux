package com.reactivespring.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebFlux;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

// Enable to access all endpoints
@WebFluxTest(controllers = FluxAndMonoController.class)
// Enable to inject the web client
@AutoConfigureWebTestClient
class FluxAndMonoControllerTest {

    @Autowired
    WebTestClient webTestClient;

    /**
     * Approach 1, use webTestClient and expect all the stuff with it
     */
    @Test
    void flux_approach1() {
        webTestClient.get()
                .uri("/flux")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Integer.class)
                .hasSize(3);
    }

    /**
     * Approach 2, use webTestClient to receive the http response, then us StepVerifier to verify the response
     */
    @Test
    void flux_approach2() {
        var fluxResp = webTestClient.get()
                .uri("/flux")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .returnResult(Integer.class)
                .getResponseBody();

        StepVerifier.create(fluxResp)
                .expectNext(1, 2, 3)
                .verifyComplete();
    }

    @Test
    void flux_approach3() {
        webTestClient.get()
                .uri("/flux")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Integer.class)
                .consumeWith(res -> {
                    List<Integer> responseBody = res.getResponseBody();
                    assert Objects.requireNonNull(responseBody).size() == 3;
                });
    }

}