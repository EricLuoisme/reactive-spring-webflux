package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class FluxAndMonoGeneratorService {

    public Flux<String> namesFlux() {
        return Flux.fromIterable(List.of("alex", "tom", "tim"));
    }

    public Mono<String> nameMono() {
        return Mono.just("patrick");
    }

    public static void main(String[] args) {
        FluxAndMonoGeneratorService t = new FluxAndMonoGeneratorService();
        // we can having the Flux data, only when we subscribe it
        t.namesFlux()
                .subscribe(n -> System.out.println("Name is " + n));
        // same as the Mono data
        t.nameMono()
                .subscribe(n -> System.out.println("Mono name is " + n));
    }

}
