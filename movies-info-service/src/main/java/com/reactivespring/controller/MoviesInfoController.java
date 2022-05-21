package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MoviesInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import javax.validation.Valid;

/**
 * For the WebFlux response, if we put noting (not specifically put @ResponseStatus),
 * even an error occur inside, we still response on HttpStatus:2xx
 */
@Slf4j
@RestController
@RequestMapping("/v1")
public class MoviesInfoController {

    @Autowired
    private MoviesInfoService moviesInfoService;

    private Sinks.Many<MovieInfo> moviesInfoSink = Sinks.many().replay().all();


    @GetMapping("/movieinfos")
    public Flux<MovieInfo> getAllMovieInfos(@RequestParam(value = "year", required = false) Integer year,
                                            @RequestParam(value = "name", required = false) String name) {

        if (null != year) {
            return moviesInfoService.getMovieInfoByYear(year);
        }

        return moviesInfoService.getAllMovieInfos()
                .log();
    }

    @GetMapping("/movieinfos_2")
    public Mono<MovieInfo> getAllMovieInfos_2(@RequestParam(value = "name", required = false) String name) {
        return moviesInfoService.getMovieInfoByName(name)
                .log();
    }

    @GetMapping("/movieinfos/{id}")
    public Mono<MovieInfo> getMovieInfosById(@PathVariable String id) {
        return moviesInfoService.getMovieInfoById(id)
                .log();
    }

    /**
     * By convert Mono-MovieInfo into Mono-ResponseEntity-MovieInfo,
     * we could get much more clear response when facing error
     */
    @PutMapping("/movieinfos/{id}")
    public Mono<ResponseEntity<MovieInfo>> updateMovieInfosById(@RequestBody @Valid MovieInfo inputMovieInfo, @PathVariable String id) {
        return moviesInfoService.updateMovieIndo(inputMovieInfo, id)
                .map(ResponseEntity.ok()::body)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .log();
    }


    @GetMapping(value = "/movieinfos/streams", produces = MediaType.APPLICATION_JSON_VALUE)
    public Flux<MovieInfo> getAllMovieInfosStream() {
        return moviesInfoSink.asFlux();
    }

    @PostMapping("/movieinfos")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfo> addMovieInfo(@RequestBody @Valid MovieInfo movieInfo) {
        return moviesInfoService.addMovieInfo(movieInfo)
                // after the Consumer has consume it
                // just adding it into the sink
                .doOnNext(saveInfo -> {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    log.info(">>> Start adding into the Sink");
                    moviesInfoSink.tryEmitNext(saveInfo);
                })
                .log();
    }

    @DeleteMapping("/movieinfos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteMovieInfoById(@PathVariable String id) {
        return moviesInfoService.deleteMovieInfo(id);
    }
}
