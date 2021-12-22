package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Random;

public class FluxAndMonoGeneratorService {

    public Flux<String> namesFlux() {
        return Flux.fromIterable(List.of("alex", "tom", "tim")).log();
    }

    public Flux<String> namesFluxNum(int strLen) {
        return Flux.fromIterable(List.of("alex", "tom", "tim"))
                .filter(str -> str.length() > strLen);
    }

    public Mono<String> nameMono() {
        return Mono.just("patrick").log();
    }

    public Flux<String> namesFluxMap() {
        return Flux.fromIterable(List.of("alex", "tom", "tim"))
                .map(String::toUpperCase);
    }

    public Flux<String> namesFluxFlatMapAsync() {
        return Flux.fromIterable(List.of("alex", "tom", "tim"))
                .map(String::toUpperCase)
                .concatMap(str -> splitString(str))
                .log();
    }

    // flatmap is async, take care of it when one the mapping stuff might have delay
    // if it might have delay, we have to use ConcatMap (in order) instead of FlatMap (not in order)
    private Flux<String> splitString(String name) {
        String[] split = name.split("");
        int delayMs = new Random().nextInt(1000);
        // it is asynchronized, which means it will not in order
        return Flux.fromArray(split)
                .delayElements(Duration.ofMillis(delayMs));
    }

    public Flux<String> namesFluxImmutable() {
        Flux<String> stringFlux = Flux.fromIterable(List.of("alex", "tom", "tim"));
        stringFlux.map(String::toUpperCase);
        return stringFlux;
    }

    public static void main(String[] args) {
        FluxAndMonoGeneratorService t = new FluxAndMonoGeneratorService();

//        // we can having the Flux data, only when we subscribe it
//        t.namesFlux()
//                .subscribe(n -> System.out.println("Name is " + n));

//        // same as the Mono data
//        t.nameMono()
//                .subscribe(n -> System.out.println("Mono name is " + n));

        // simple map
        t.namesFluxFlatMapAsync().subscribe();

//        // immutable test
//        t.namesFluxImmutable().subscribe(n -> System.out.println("Name is " + n));

//        // filter test
//        t.namesFluxNum(3).subscribe(n -> System.out.println("Name is " + n));
    }

}
