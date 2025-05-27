package com.pryabykh.intershop.controller;

import com.pryabykh.intershop.client.PaymentApiClient;
import com.pryabykh.intershop.client.domain.PayPost200Response;
import com.pryabykh.intershop.entity.CartItem;
import com.pryabykh.intershop.entity.Image;
import com.pryabykh.intershop.entity.Item;
import com.pryabykh.intershop.entity.Order;
import com.pryabykh.intershop.repository.CartItemRepository;
import com.pryabykh.intershop.repository.ImageRepository;
import com.pryabykh.intershop.repository.ItemRepository;
import com.pryabykh.intershop.repository.OrderRepository;
import com.pryabykh.intershop.service.CacheService;
import com.pryabykh.intershop.service.UserService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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

    @MockitoBean
    private PaymentApiClient paymentApiClient;

    @Autowired
    private CacheService cacheService;

    @BeforeEach
    void setUp() {
        cacheService.evictAllCaches().block();
        Mockito.reset(paymentApiClient);
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
                .then();

        cleanup.block();
    }

    @Test
    void createOrderRedirectToLoginIfUnauthorized() {
        webTestClient.post()
                .uri("/buy")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().value("Location", Matchers.equalTo("/login"));
    }

    @Test
    @WithMockUser(username = "user1", roles = {"CUSTOMER"})
    void createOrder() throws Exception {
        when(paymentApiClient.payPost(any()))
                .thenReturn(Mono.just(new PayPost200Response().newBalance(1L).message("success").success(true)));
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
        cartItem.setUserId(userService.fetchCurrentUserId().block());

        cartItemRepository.save(cartItem).block();

        List<CartItem> block = cartItemRepository.findAll().collectList().block();

        webTestClient.post()
                .uri("/buy")
                .exchange()
                .expectStatus().is3xxRedirection();
    }

    @Test
    void fetchOrderRedirectToLoginIfUnauthorized() {
        webTestClient.get()
                .uri("/orders/" + 1L)
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().value("Location", Matchers.equalTo("/login"));
    }

    @Test
    @WithMockUser(username = "user1", roles = {"CUSTOMER"})
    void fetchOrder() throws Exception {
        when(paymentApiClient.payPost(any()))
                .thenReturn(Mono.just(new PayPost200Response().newBalance(1L).message("success").success(true)));
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
        cartItem.setUserId(userService.fetchCurrentUserId().block());
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
    void fetchOrdersRedirectToLoginIfUnauthorized() {
        webTestClient.get()
                .uri("/orders")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().value("Location", Matchers.equalTo("/login"));
    }

    @Test
    @WithMockUser(username = "user1", roles = {"CUSTOMER"})
    void fetchOrders() throws Exception {
        when(paymentApiClient.payPost(any()))
                .thenReturn(Mono.just(new PayPost200Response().newBalance(1L).message("success").success(true)));
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
        cartItem.setUserId(userService.fetchCurrentUserId().block());
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
