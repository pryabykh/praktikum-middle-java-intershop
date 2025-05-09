package com.pryabykh.payment.repository;

import com.pryabykh.payment.SpringBootPostgreSQLTestContainerBaseTest;
import com.pryabykh.payment.entity.Account;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class AccountRepositoryTest extends SpringBootPostgreSQLTestContainerBaseTest {

    @Autowired
    private AccountRepository accountRepository;

    @Test
    void findById_whenEntityExists_shouldReturnIt() {
        Mono<Account> accountMono = accountRepository.findByUserId(1L)
                .flatMap(image -> accountRepository.findById(image.getId()));

        StepVerifier.create(accountMono)
                .assertNext(image -> {
                    assertNotNull(image);
                    assertNotNull(image.getId());
                    assertNotNull(image.getBalance());
                })
                .expectComplete()
                .verify();
    }
}
