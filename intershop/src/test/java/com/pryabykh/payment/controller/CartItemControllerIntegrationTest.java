package com.pryabykh.payment.controller;

import com.pryabykh.payment.entity.CartItem;
import com.pryabykh.payment.entity.Image;
import com.pryabykh.payment.entity.Item;
import com.pryabykh.payment.entity.User;
import com.pryabykh.payment.repository.CartItemRepository;
import com.pryabykh.payment.repository.ImageRepository;
import com.pryabykh.payment.repository.ItemRepository;
import com.pryabykh.payment.repository.UserRepository;
import com.pryabykh.payment.service.CacheService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class CartItemControllerIntegrationTest extends WebFluxPostgreSQLTestContainerBaseTest {

    @Autowired
    @MockitoSpyBean
    private CartItemRepository cartItemRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DatabaseClient databaseClient;

    @Autowired
    @MockitoSpyBean
    private CacheService cacheService;

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
    void modifyCartAndRedirectToMain() {
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

        User user = new User("admin");
        Long userId = userRepository.save(user).block().getId();

        CartItem cartItem = new CartItem();
        cartItem.setItemId(savedItem.getId());
        cartItem.setCount(1);
        cartItem.setUserId(userId);
        cartItemRepository.save(cartItem).block();

        webTestClient.get()
                .uri("/modify/main/items/" + savedItem.getId() + "?action=plus")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().value("Location", Matchers.equalTo("/main/items"));

        verify(cacheService, times(1)).evictCaches(anyLong(), anyLong());
    }

    @Test
    void modifyCartAndRedirectToItem() {
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

        User user = new User("admin");
        Long userId = userRepository.save(user).block().getId();

        CartItem cartItem = new CartItem();
        cartItem.setItemId(savedItem.getId());
        cartItem.setCount(1);
        cartItem.setUserId(userId);
        cartItemRepository.save(cartItem).block();

        webTestClient.get()
                .uri("/modify/items/" + savedItem.getId() + "?action=plus")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().value("Location", Matchers.equalTo("/items/" + savedItem.getId()));

        verify(cacheService, times(1)).evictCaches(anyLong(), anyLong());
    }

    @Test
    void modifyCartAndRedirectToCart() {
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

        User user = new User("admin");
        Long userId = userRepository.save(user).block().getId();

        CartItem cartItem = new CartItem();
        cartItem.setItemId(savedItem.getId());
        cartItem.setCount(1);
        cartItem.setUserId(userId);
        cartItemRepository.save(cartItem).block();

        webTestClient.get()
                .uri("/modify/cart/items/" + savedItem.getId() + "?action=plus")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().value("Location", Matchers.equalTo("/cart/items"));

        verify(cacheService, times(1)).evictCaches(anyLong(), anyLong());
    }

    @Test
    void fetchCartItems() {
        // Setup data
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

        User user = new User("admin");
        Long userId = userRepository.save(user).block().getId();

        CartItem cartItem = new CartItem();
        cartItem.setItemId(savedItem.getId());
        cartItem.setCount(1);
        cartItem.setUserId(userId);
        cartItemRepository.save(cartItem).block();

        webTestClient.get()
                .uri("/cart/items")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody();

        webTestClient.get()
                .uri("/cart/items")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody();

        verify(cartItemRepository, times(1)).findByUserIdOrderByIdDesc(anyLong());
    }
}
