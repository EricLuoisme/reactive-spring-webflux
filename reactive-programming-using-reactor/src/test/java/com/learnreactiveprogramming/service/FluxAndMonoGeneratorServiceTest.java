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
}