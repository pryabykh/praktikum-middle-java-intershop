package com.pryabykh.intershop.repository;

import com.pryabykh.intershop.entity.Order;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.util.List;

@Repository
public interface OrderRepository extends R2dbcRepository<Order, Long> {

    Flux<Order> findByUserIdOrderByIdDesc(Long userId);
}
