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

    /**
     * Approach 3, consume the Flux response, and verify them one by one
     */
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

    @Test
    void mono_approach1() {
        webTestClient.get()
                .uri("/mono")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(String.class)
                .consumeWith(res -> {
                    var responseBody = res.getResponseBody();
                    assertEquals("Hello World", responseBody);
                });
    }

    /**
     * For stream, we have to use StepVerifier and calling the thenCancel() function, to stop the stream
     */
    @Test
    void stream_approach() {
        var fluxResp = webTestClient.get()
                .uri("/stream")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .returnResult(Long.class)
                .getResponseBody();

        StepVerifier.create(fluxResp)
                .expectNext(0L, 1L, 2L, 3L)
                .thenCancel()
        .verify();
    }

}