package com.pryabykh.intershop.repository;

import com.pryabykh.intershop.entity.Item;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ItemRepository extends R2dbcRepository<Item, Long> {

    @Query("SELECT * FROM intershop.items i WHERE LOWER(i.title) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY i.id desc LIMIT :limit OFFSET :offset")
    Flux<Item> findAllByNameLikeOrderByIdDesc(String name, int limit, int offset);

    @Query("SELECT * FROM intershop.items i WHERE LOWER(i.title) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY i.title LIMIT :limit OFFSET :offset")
    Flux<Item> findAllByNameLikeOrderByTitleAsc(String name, int limit, int offset);

    @Query("SELECT * FROM intershop.items i WHERE LOWER(i.title) LIKE LOWER(CONCAT('%', :name, '%')) ORDER BY i.price LIMIT :limit OFFSET :offset")
    Flux<Item> findAllByNameLikeOrderByPriceAsc(String name, int limit, int offset);

    @Query("SELECT COUNT(*) FROM intershop.items WHERE LOWER(title) LIKE LOWER(CONCAT('%', :name, '%'))")
    Mono<Long> countByNameLike(String name);

    @Query("SELECT * FROM intershop.items i ORDER BY i.id desc LIMIT :limit OFFSET :offset")
    Flux<Item> findAllOrderByIdDesc(int limit, int offset);

    @Query("SELECT * FROM intershop.items i ORDER BY i.title LIMIT :limit OFFSET :offset")
    Flux<Item> findAllOrderByTitleAsc(int limit, int offset);

    @Query("SELECT * FROM intershop.items i ORDER BY i.price LIMIT :limit OFFSET :offset")
    Flux<Item> findAllOrderByPriceAsc(int limit, int offset);

    @Query("SELECT COUNT(*) FROM intershop.items")
    Mono<Long> count();
}
