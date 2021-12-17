package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;

import java.util.List;

public class FluxAndMonoGeneratorService {

    public Flux<String> namesFlux() {
        return Flux.fromIterable(List.of("alex", "tom", "tim"));
    }

    public static void main(String[] args) {
        FluxAndMonoGeneratorService t = new FluxAndMonoGeneratorService();
        // we can having the flux data, only when we subscribe it
        t.namesFlux()
                .subscribe(n -> System.out.println("Name is " + n));
    }

}
