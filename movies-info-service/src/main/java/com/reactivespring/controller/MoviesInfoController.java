package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MoviesInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

/**
 * For the WebFlux response, if we put noting (not specifically put @ResponseStatus),
 * even an error occur inside, we still response on HttpStatus:2xx
 */
@RestController
@RequestMapping("/v1")
public class MoviesInfoController {

    @Autowired
    private MoviesInfoService moviesInfoService;


    @GetMapping("/movieinfos")
    public Flux<MovieInfo> getAllMovieInfos(@RequestParam(value = "year", required = false) Integer year,
                                            @RequestParam(value = "name", required = false) String name) {

        if (null != year) {
            return moviesInfoService.getMovieInfoByYear(year);
        }

        return moviesInfoService.getAllMovieInfos()
                .log();
    }

    @GetMapping("/movieinfos")
    public Mono<MovieInfo> getAllMovieInfos(@RequestParam(value = "name", required = false) String name) {
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

    @PostMapping("/movieinfos")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfo> addMovieInfo(@RequestBody @Valid MovieInfo movieInfo) {
        return moviesInfoService.addMovieInfo(movieInfo)
                .log();
    }

    @DeleteMapping("/movieinfos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteMovieInfoById(@PathVariable String id) {
        return moviesInfoService.deleteMovieInfo(id);
    }
}
