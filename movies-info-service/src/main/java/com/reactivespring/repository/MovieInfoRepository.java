package com.reactivespring.repository;


import com.reactivespring.domain.MovieInfo;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Component;

@Component
public interface MovieInfoRepository extends ReactiveMongoRepository<MovieInfo, String> {

}
