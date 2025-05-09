package com.pryabykh.payment.repository;

import com.pryabykh.payment.entity.Order;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

@Repository
public interface OrderRepository extends R2dbcRepository<Order, Long> {

    Flux<Order> findByUserIdOrderByIdDesc(Long userId);
}
