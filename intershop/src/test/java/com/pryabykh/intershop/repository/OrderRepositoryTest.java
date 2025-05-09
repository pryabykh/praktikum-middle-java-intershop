package com.pryabykh.intershop.repository;

import com.pryabykh.intershop.SpringBootPostgreSQLTestContainerBaseTest;
import com.pryabykh.intershop.entity.Order;
import com.pryabykh.intershop.entity.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OrderRepositoryTest extends SpringBootPostgreSQLTestContainerBaseTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;
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
                        insert into intershop.items (title, price, description, image_id) values ('Samsung 10', 100, 'Samsung 10', (select id from intershop.images order by id limit 1));
                        """)
                        .fetch()
                        .rowsUpdated())
                .then(databaseClient.sql("""
                        insert into intershop.orders (total_sum, user_id) values (100, (select id from intershop.users order by id limit 1));
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
    void findByUserIdOrderByIdDesc_whenEntityExists_shouldReturnIt() {
        Mono<Long> userIdMono = userRepository.findAll()
                .next()
                .map(User::getId);

        Flux<Order> ordersFlux = userIdMono.flatMapMany(orderRepository::findByUserIdOrderByIdDesc);

        StepVerifier.create(ordersFlux)
                .assertNext(order -> {
                    assertNotNull(order);
                    assertNotNull(order.getId());
                })
                .expectNextCount(0)
                .verifyComplete();
    }

}
