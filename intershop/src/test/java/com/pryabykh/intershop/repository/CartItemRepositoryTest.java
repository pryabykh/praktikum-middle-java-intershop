package com.pryabykh.intershop.repository;

import com.pryabykh.intershop.SpringBootPostgreSQLTestContainerBaseTest;
import com.pryabykh.intershop.entity.CartItem;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class CartItemRepositoryTest extends SpringBootPostgreSQLTestContainerBaseTest {

    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private DatabaseClient databaseClient;

    @BeforeEach
    void setUp() {
        Mono<Void> setup = databaseClient.sql("""
                INSERT INTO intershop.users (name) VALUES ('ADMIN');
                """)
                .fetch()
                .rowsUpdated()
                .then(databaseClient.sql("""
                        INSERT INTO intershop.images (name, bytes) VALUES ('small_image.png', DECODE('74657374', 'hex'));
                        """)
                        .fetch()
                        .rowsUpdated())
                .then(databaseClient.sql("""
                        INSERT INTO intershop.items (title, price, description, image_id)
                        VALUES ('IPhone 1', 100, 'IPhone 1', (SELECT id FROM intershop.images ORDER BY id LIMIT 1));
                        """)
                        .fetch()
                        .rowsUpdated())
                .then(databaseClient.sql("""
                        INSERT INTO intershop.carts (user_id, item_id, count)
                        VALUES ((SELECT id FROM intershop.users ORDER BY id LIMIT 1),
                                (SELECT id FROM intershop.items ORDER BY id LIMIT 1), 1);
                        """)
                        .fetch()
                        .rowsUpdated())
                .then();

        setup.block();
    }

    @AfterEach
    void tearDown() {
        // Delete all data using native SQL queries
        Mono<Void> cleanup = databaseClient.sql("DELETE FROM intershop.carts;")
                .fetch()
                .rowsUpdated()
                .then(databaseClient.sql("DELETE FROM intershop.order_items;").fetch().rowsUpdated())
                .then(databaseClient.sql("DELETE FROM intershop.orders;").fetch().rowsUpdated())
                .then(databaseClient.sql("DELETE FROM intershop.items;").fetch().rowsUpdated())
                .then(databaseClient.sql("DELETE FROM intershop.images;").fetch().rowsUpdated())
                .then(databaseClient.sql("DELETE FROM intershop.users;").fetch().rowsUpdated())
                .then();

        cleanup.block(); // Block to ensure cleanup completes after each test
    }

    @Test
    void findByItemIdAndUserId_whenEntityExists_shouldReturnIt() {
        Long userId = userRepository.findAll().blockFirst().getId();
        Long itemId = itemRepository.findAll().blockFirst().getId();

        Mono<CartItem> cartItemMono = cartItemRepository.findByItemIdAndUserId(itemId, userId);

        StepVerifier.create(cartItemMono)
                .assertNext(cartItem -> assertNotNull(cartItem))
                .expectComplete()
                .verify();
    }

    @Test
    void findByItemIdInAndUserId_whenEntityExists_shouldReturnIt() {
        Long userId = userRepository.findAll().blockFirst().getId();
        Long itemId = itemRepository.findAll().blockFirst().getId();

        Flux<CartItem> cartItemsFlux = cartItemRepository.findByItemIdInAndUserId(List.of(itemId), userId);

        StepVerifier.create(cartItemsFlux)
                .assertNext(cartItem -> assertNotNull(cartItem))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void findByUserIdOrderByIdDesc_whenEntityExists_shouldReturnIt() {
        Long userId = userRepository.findAll().blockFirst().getId();

        Flux<CartItem> cartItemsFlux = cartItemRepository.findByUserIdOrderByIdDesc(userId);

        StepVerifier.create(cartItemsFlux)
                .assertNext(cartItem -> assertNotNull(cartItem))
                .expectNextCount(0)
                .verifyComplete();
    }

    @Test
    void deleteByUserId_whenEntityExists_shouldDeleteIt() {
        Long userId = userRepository.findAll().blockFirst().getId();

        Mono<Void> deleteResult = cartItemRepository.deleteByUserId(userId);

        StepVerifier.create(deleteResult)
                .verifyComplete();

        StepVerifier.create(cartItemRepository.findAll())
                .expectNextCount(0)
                .verifyComplete();
    }
}
