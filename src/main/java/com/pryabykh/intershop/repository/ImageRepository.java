package com.pryabykh.intershop.repository;

import com.pryabykh.intershop.entity.Image;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface ImageRepository extends R2dbcRepository<Image, Long> {

    @Override
    @Cacheable(
            value = "images",
            key = "#id"
    )
    Mono<Image> findById(Long id);
}
