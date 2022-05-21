package com.reactivespring.controller;


import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

public class SinkTest {

    @Test
    public void sink() {

        Sinks.Many<Integer> replaySink = Sinks.many().replay().all();

        replaySink.emitNext(1, Sinks.EmitFailureHandler.FAIL_FAST);
        replaySink.emitNext(2, Sinks.EmitFailureHandler.FAIL_FAST);

        Flux<Integer> integerFlux = replaySink.asFlux();
        integerFlux.subscribe(i -> {
            System.out.println("Subscriber 1: " + i);
        });

        Flux<Integer> integerFlux_2 = replaySink.asFlux();
        integerFlux_2.subscribe(i -> {
            System.out.println("Subscriber 2: " + i);
        });

        // another emitting
        System.out.println("\n Another emission occur \n");
        replaySink.emitNext(3, Sinks.EmitFailureHandler.FAIL_FAST);

        // cause we using the replay, later subscriber would also have receive the previous emission
        Flux<Integer> integerFlux_3 = replaySink.asFlux();
        integerFlux_3.subscribe(i -> {
            System.out.println("Subscriber 3: " + i);
        });

    }

    @Test
    public void sink_multiCast() {
        Sinks.many().multicast().onBackpressureBuffer();


    }

}
