package com.pryabykh.intershop.repository;

import com.pryabykh.intershop.SpringBootPostgreSQLTestContainerBaseTest;
import com.pryabykh.intershop.entity.Item;
import com.pryabykh.intershop.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ItemRepositoryTest extends SpringBootPostgreSQLTestContainerBaseTest {

    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private DatabaseClient databaseClient;

    @BeforeEach
    void setUp() {
        Mono<Void> setup = databaseClient.sql("""
                insert into intershop.users (name) values ('ADMIN')
                """)
                .fetch()
                .rowsUpdated()
                .then(databaseClient.sql("""
                        insert into intershop.images (name, bytes) values ('small_image.png', decode('74657374', 'hex'))
                        """)
                        .fetch()
                        .rowsUpdated())
                .then(databaseClient.sql("""
                        insert into intershop.items (title, price, description, image_id) values ('IPhone 1', 100, 'IPhone 1', (select id from intershop.images order by id limit 1));
                        """)
                        .fetch()
                        .rowsUpdated())
                .then(databaseClient.sql("""
                        insert into intershop.items (title, price, description, image_id) values ('IPhone 2', 100, 'IPhone 2', (select id from intershop.images order by id limit 1));
                        """)
                        .fetch()
                        .rowsUpdated())
                .then(databaseClient.sql("""
                        insert into intershop.items (title, price, description, image_id) values ('IPhone 3', 100, 'IPhone 3', (select id from intershop.images order by id limit 1));
                        """)
                        .fetch()
                        .rowsUpdated())
                .then(databaseClient.sql("""
                        insert into intershop.items (title, price, description, image_id) values ('Samsung 10', 100, 'Samsung 10', (select id from intershop.images order by id limit 1));
                        """)
                        .fetch()
                        .rowsUpdated())
                .then();

        setup.block();
    }

    @AfterEach
    void tearDown() {

        Mono<Void> cleanup = databaseClient.sql("DELETE FROM intershop.carts;")
                .fetch()
                .rowsUpdated()
                .then(databaseClient.sql("DELETE FROM intershop.order_items;").fetch().rowsUpdated())
                .then(databaseClient.sql("DELETE FROM intershop.orders;").fetch().rowsUpdated())
                .then(databaseClient.sql("DELETE FROM intershop.items;").fetch().rowsUpdated())
                .then(databaseClient.sql("DELETE FROM intershop.images;").fetch().rowsUpdated())
                .then(databaseClient.sql("DELETE FROM intershop.users;").fetch().rowsUpdated())
                .then();

        cleanup.block();
    }

    @Test
    void findAllByNameLike_whenTableHasRowsWithFetchedName_shouldReturnRows() {
        Flux<Item> itemsFlux = userService.fetchCurrentUserId().flatMapMany(userId -> {
            return itemRepository.findAllByNameLikeOrderByIdDesc(userId, "iphone", 10, 0);
        });

        StepVerifier.create(itemsFlux)
                .assertNext(item -> {
                    assertNotNull(item);
                    assertNotNull(item.getId());
                })
                .expectNextCount(2)
                .expectComplete()
                .verify();
    }

    @Test
    void findAllByNameLike_whenTableDoesNotHaveRowsWithFetchedName_shouldReturnEmptyResult() {
        Flux<Item> itemsFlux = userService.fetchCurrentUserId().flatMapMany(userId -> {
            return itemRepository.findAllByNameLikeOrderByIdDesc(userId, "nonexistent", 0, 10);
        });

        StepVerifier.create(itemsFlux)
                .expectNextCount(0)
                .verifyComplete();
    }
}
