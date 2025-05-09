package com.pryabykh.payment.repository;

import com.pryabykh.payment.entity.Account;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Mono;

public interface AccountRepository extends ReactiveCrudRepository<Account, Long> {
    Mono<Account> findByUserId(Long userId);
}
