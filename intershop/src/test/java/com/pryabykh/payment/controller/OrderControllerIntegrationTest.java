package com.pryabykh.payment.controller;

import com.pryabykh.payment.entity.CartItem;
import com.pryabykh.payment.entity.Image;
import com.pryabykh.payment.entity.Item;
import com.pryabykh.payment.entity.Order;
import com.pryabykh.payment.repository.CartItemRepository;
import com.pryabykh.payment.repository.ImageRepository;
import com.pryabykh.payment.repository.ItemRepository;
import com.pryabykh.payment.repository.OrderRepository;
import com.pryabykh.payment.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class OrderControllerIntegrationTest extends WebFluxPostgreSQLTestContainerBaseTest {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private DatabaseClient databaseClient;

    @AfterEach
    void tearDown() {
        Mono<Void> cleanup = databaseClient.sql("DELETE FROM intershop.carts;")
                .fetch()
                .rowsUpdated()
                .then(databaseClient.sql("DELETE FROM intershop.order_items;").fetch().rowsUpdated())
                .then(databaseClient.sql("DELETE FROM intershop.orders;").fetch().rowsUpdated())
                .then(databaseClient.sql("DELETE FROM intershop.items;").fetch().rowsUpdated())
                .then(databaseClient.sql("DELETE FROM intershop.images;").fetch().rowsUpdated())
                .then();

        cleanup.block();
    }

    @Test
    void createOrder() throws Exception {
        Image image = new Image();
        image.setName("n");
        image.setBytes("b".getBytes(StandardCharsets.UTF_8));
        Long imageId = imageRepository.save(image).block().getId();

        Item item = new Item();
        item.setPrice(1L);
        item.setDescription("d");
        item.setImageId(imageId);
        item.setTitle("t");
        Item savedItem = itemRepository.save(item).block();

        CartItem cartItem = new CartItem();
        cartItem.setItemId(savedItem.getId());
        cartItem.setCount(1);
        cartItem.setUserId(userService.fetchDefaultUserId().block());
        cartItemRepository.save(cartItem).block();

        List<CartItem> block = cartItemRepository.findAll().collectList().block();

        webTestClient.post()
                .uri("/buy")
                .exchange()
                .expectStatus().is3xxRedirection();
    }

    @Test
    void fetchOrder() throws Exception {
        Image image = new Image();
        image.setName("n");
        image.setBytes("b".getBytes(StandardCharsets.UTF_8));
        Long imageId = imageRepository.save(image).block().getId();

        Item item = new Item();
        item.setPrice(1L);
        item.setDescription("d");
        item.setImageId(imageId);
        item.setTitle("t");
        Item savedItem = itemRepository.save(item).block();

        CartItem cartItem = new CartItem();
        cartItem.setItemId(savedItem.getId());
        cartItem.setCount(1);
        cartItem.setUserId(userService.fetchDefaultUserId().block());
        cartItemRepository.save(cartItem).block();

        webTestClient.post()
                .uri("/buy")
                .exchange()
                .expectStatus().is3xxRedirection();

        Long orderId = orderRepository.findAll().map(Order::getId).blockFirst();

        webTestClient.get()
                .uri("/orders/" + orderId)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html");
    }

    @Test
    void fetchOrders() throws Exception {
        Image image = new Image();
        image.setName("n");
        image.setBytes("b".getBytes(StandardCharsets.UTF_8));
        Long imageId = imageRepository.save(image).block().getId();

        Item item = new Item();
        item.setPrice(1L);
        item.setDescription("d");
        item.setImageId(imageId);
        item.setTitle("t");
        Item savedItem = itemRepository.save(item).block();

        CartItem cartItem = new CartItem();
        cartItem.setItemId(savedItem.getId());
        cartItem.setCount(1);
        cartItem.setUserId(userService.fetchDefaultUserId().block());
        cartItemRepository.save(cartItem).block();

        webTestClient.post()
                .uri("/buy")
                .exchange()
                .expectStatus().is3xxRedirection();

        Long orderId = orderRepository.findAll().map(Order::getId).blockFirst();

        webTestClient.get()
                .uri("/orders")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html");
    }
}
