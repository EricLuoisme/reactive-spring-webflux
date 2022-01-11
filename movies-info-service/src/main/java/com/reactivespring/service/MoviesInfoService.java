package com.reactivespring.service;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.repository.MovieInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class MoviesInfoService {

    @Autowired
    private MovieInfoRepository movieInfoRepository;


    public Mono<MovieInfo> addMovieInfo(MovieInfo movieInfo) {
        return movieInfoRepository.save(movieInfo);
    }

    public Flux<MovieInfo> getAllMovieInfos() {
        return movieInfoRepository.findAll();
    }

    public Mono<MovieInfo> getMovieInfoById(String id) {
        return movieInfoRepository.findById(id);
    }

    public Mono<MovieInfo> updateMovieIndo(MovieInfo movieInfo) {
        return movieInfoRepository.findById(movieInfo.getMovieInfoId())
                .flatMap(stored -> {
                    stored.setCast(movieInfo.getCast());
                    stored.setName(movieInfo.getName());
                    stored.setRelease_date(movieInfo.getRelease_date());
                    stored.setYear(movieInfo.getYear());
                    return movieInfoRepository.save(stored);
                });
    }

    public Mono<Void> deleteMovieInfo(String id) {
        return movieInfoRepository.deleteById(id);
    }
}
