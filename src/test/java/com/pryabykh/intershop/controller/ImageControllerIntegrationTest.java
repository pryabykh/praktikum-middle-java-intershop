package com.pryabykh.intershop.controller;

import com.pryabykh.intershop.entity.Image;
import com.pryabykh.intershop.repository.CartItemRepository;
import com.pryabykh.intershop.repository.ImageRepository;
import com.pryabykh.intershop.repository.ItemRepository;
import com.pryabykh.intershop.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

public class ImageControllerIntegrationTest extends WebFluxPostgreSQLTestContainerBaseTest {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    protected DatabaseClient databaseClient;

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
    void modifyCartAndRedirectToMain() throws Exception {
        Image image = new Image();
        image.setName("n");
        image.setBytes("b".getBytes(StandardCharsets.UTF_8));
        Long imageId = imageRepository.save(image).block().getId();

        webTestClient.get()
                .uri("/images/" + imageId)
                .exchange()
                .expectStatus().isOk();
    }
}
