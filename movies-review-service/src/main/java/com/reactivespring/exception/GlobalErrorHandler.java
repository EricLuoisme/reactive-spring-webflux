package com.reactivespring.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;


@Slf4j
@Component
public class GlobalErrorHandler implements ErrorWebExceptionHandler {

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {

        log.error("Exception message : {}", ex.getMessage(), ex);

        // extract the error message and prepare the buffer factory to store it
        DataBufferFactory dataBufferFactory = exchange.getResponse().bufferFactory();
        var errorMsg = dataBufferFactory.wrap(ex.getMessage().getBytes(StandardCharsets.UTF_8));

        if (ex instanceof ReviewDataException) {
            // rewrite the response from 500 internal -> 400 bad request
            exchange.getResponse().setStatusCode(HttpStatus.BAD_REQUEST);
        }

        return exchange.getResponse().writeWith(Mono.just(errorMsg));
    }
}
