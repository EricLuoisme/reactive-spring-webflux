package com.reactivespring.restclient;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.exception.MoviesInfoClientException;
import com.reactivespring.exception.MoviesInfoServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Exceptions;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;

@Component
public class MoviesInfoRestClient {

    @Autowired
    private WebClient webClient;

    @Value("${restClient.moviesInfoUrl}")
    private String moviesInfoUrl;

    /** Retry Strategy */
    private RetryBackoffSpec retryBackoffSpec = Retry.fixedDelay(3, Duration.ofSeconds(1))
            // would throw exception if reach the max retry-times
            .onRetryExhaustedThrow((
                    // propagate the error
                    (retryBackoffSpec1, retrySignal) -> Exceptions.propagate(retrySignal.failure())));


    public Mono<MovieInfo> retrieveMovieInfo(String movieId) {





        String url = moviesInfoUrl.concat("/{id}");

        return webClient.get()
                .uri(url, movieId)
                .retrieve()
                // take care of exception (error resp)
                .onStatus(HttpStatus::is4xxClientError, clientResponse -> {
                    if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        return Mono.error(
                                new MoviesInfoClientException("There is no MovieInfo available for the passed id: " + movieId,
                                        clientResponse.statusCode().value()));
                    }
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(responseMsg ->
                                    Mono.error(
                                            new MoviesInfoClientException(responseMsg, clientResponse.statusCode().value())));
                })
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> {
                    if (clientResponse.statusCode().equals(HttpStatus.NOT_FOUND)) {
                        return Mono.error(
                                new MoviesInfoServerException("Server Exception in MovieInfo service : " + clientResponse));
                    }
                    return clientResponse.bodyToMono(String.class)
                            .flatMap(responseMsg ->
                                    Mono.error(
                                            new MoviesInfoClientException(responseMsg, clientResponse.statusCode().value())));
                })
                // take care of normal result
                .bodyToMono(MovieInfo.class)
                // retry max 3-times
//                .retry(3)
                // retry with fixed delay
                .retryWhen(retryBackoffSpec)
                .log();
    }

}
