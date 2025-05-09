package com.pryabykh.payment.repository;

import com.pryabykh.payment.SpringBootPostgreSQLTestContainerBaseTest;
import com.pryabykh.payment.entity.Image;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ImageRepositoryTest extends SpringBootPostgreSQLTestContainerBaseTest {

    @Autowired
    private ImageRepository imageRepository;
    @Autowired
    private DatabaseClient databaseClient;

    @BeforeEach
    void setUp() {
        Mono<Void> setup = databaseClient.sql("""
                        insert into intershop.images (name, bytes) values ('small_image.png', decode('74657374', 'hex'));
                        """)
                        .fetch()
                        .rowsUpdated()
                .then();

        setup.block();
    }

    @AfterEach
    void tearDown() {
        // Delete all data using native SQL queries
        Mono<Void> cleanup = databaseClient.sql("DELETE FROM intershop.images;")
                .fetch()
                .rowsUpdated()
                .then();

        cleanup.block(); // Block to ensure cleanup completes after each test
    }

    @Test
    void findById_whenEntityExists_shouldReturnIt() {
        Mono<Image> imageMono = imageRepository.findAll()
                .next()
                .flatMap(image -> imageRepository.findById(image.getId()));

        StepVerifier.create(imageMono)
                .assertNext(image -> {
                    assertNotNull(image);
                    assertNotNull(image.getId());
                    assertNotNull(image.getName());
                    assertNotNull(image.getBytes());
                })
                .expectComplete()
                .verify();
    }
}
