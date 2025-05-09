package com.pryabykh.intershop.repository;

import com.pryabykh.intershop.SpringBootPostgreSQLTestContainerBaseTest;
import com.pryabykh.intershop.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class UserRepositoryTest extends SpringBootPostgreSQLTestContainerBaseTest {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private DatabaseClient databaseClient;

    @BeforeEach
    void setUp() {
        Mono<Void> setup = databaseClient.sql("""
                insert into intershop.users (name) values ('ADMIN');
                """)
                .fetch()
                .rowsUpdated()
                .then();

        setup.block();
    }

    @AfterEach
    void tearDown() {

        Mono<Void> cleanup = databaseClient.sql("DELETE FROM intershop.users;")
                .fetch()
                .rowsUpdated()
                .then();

        cleanup.block();
    }

    @Test
    void findByItemIdAndUserId_whenEntityExists_shouldReturnIt() {
        Mono<Long> userIdMono = userRepository.findAll()
                .next()
                .map(User::getId);

        StepVerifier.create(userIdMono)
                .assertNext(userId -> {
                    assertNotNull(userId);
                })
                .expectComplete()
                .verify();
    }
}
