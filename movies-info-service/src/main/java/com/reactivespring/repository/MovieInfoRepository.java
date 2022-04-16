package com.reactivespring.repository;


import com.reactivespring.domain.MovieInfo;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovieInfoRepository extends ReactiveMongoRepository<MovieInfo, String> {

    // The ReactiveMongoRepository extends the Spring-data, thus, it follows the JPA-Standard,
    // JPA would parse the function name into related sql for execution
    Flux<MovieInfo> findByYear(Integer year);

    Mono<MovieInfo> findByName(String name);

}
