package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MoviesInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/v1")
public class MoviesInfoController {

    @Autowired
    private MoviesInfoService moviesInfoService;


    @GetMapping("/movieinfos")
    public Flux<MovieInfo> getAllMovieInfos() {
        return moviesInfoService.getAllMovieInfos()
                .log();
    }

    @GetMapping("/movieinfos/{id}")
    public Mono<MovieInfo> getMovieInfosById(@PathVariable String id) {
        return moviesInfoService.getAllMovieInfoById(id)
                .log();
    }

    @PutMapping("/movieinfos")
    public Mono<MovieInfo> updateMovieInfosById(@RequestBody MovieInfo movieInfo) {
        return moviesInfoService.updateMovieIndo(movieInfo)
                .log();
    }

    @PostMapping("/movieinfos")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfo> addMovieInfo(@RequestBody MovieInfo movieInfo) {
        return moviesInfoService.addMovieInfo(movieInfo)
                .log();
    }

    @DeleteMapping("/movieinfos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteMovieInfoById(@PathVariable String id) {
        return moviesInfoService.deleteMovieInfo(id);
    }
}
