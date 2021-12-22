package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

public class FluxAndMonoGeneratorServiceTest {

    FluxAndMonoGeneratorService test = new FluxAndMonoGeneratorService();

    @Test
    void namesFlux() {
        Flux<String> stringFlux = test.namesFlux();
        StepVerifier.create(stringFlux)
                .expectNext("alex", "tom", "tim")
                .verifyComplete();
    }

    @Test
    void testDelay() {
        Flux<String> stringFlux = test.exploreConcat();
        StepVerifier.create(stringFlux)
                .expectNext("a", "b", "c")
                .expectNext("e", "f", "g")
                .verifyComplete();
    }

    @Test
    void testMerge() {
        Flux<String> stringFlux = test.exploreMerge();
        StepVerifier.create(stringFlux)
                .expectNextCount(9)
                .verifyComplete();
    }

    @Test
    void testZip() {
        Flux<String> stringFlux = test.exploreZip();
        StepVerifier.create(stringFlux)
                .expectNextCount(3)
                .verifyComplete();
    }
}