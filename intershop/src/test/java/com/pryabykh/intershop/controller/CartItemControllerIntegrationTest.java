package com.pryabykh.intershop.controller;

import com.pryabykh.intershop.client.BalanceApiClient;
import com.pryabykh.intershop.client.domain.BalanceGet200Response;
import com.pryabykh.intershop.entity.CartItem;
import com.pryabykh.intershop.entity.Image;
import com.pryabykh.intershop.entity.Item;
import com.pryabykh.intershop.entity.User;
import com.pryabykh.intershop.repository.CartItemRepository;
import com.pryabykh.intershop.repository.ImageRepository;
import com.pryabykh.intershop.repository.ItemRepository;
import com.pryabykh.intershop.repository.UserRepository;
import com.pryabykh.intershop.service.CacheService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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

    @MockitoBean
    private BalanceApiClient balanceApiClient;

    @Autowired
    @MockitoSpyBean
    private CacheService cacheService;

    @BeforeEach
    void setUp() {
        cacheService.evictAllCaches().block();
        Mockito.reset(cartItemRepository, cacheService, balanceApiClient);
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
    void modifyCartMainRedirectToLoginIfUnauthorized() {
        webTestClient.get()
                .uri("/modify/main/items/" + 1L + "?action=plus")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().value("Location", Matchers.equalTo("/login"));
    }

    @Test
    @WithMockUser(username = "user1", roles = {"CUSTOMER"})
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
    void modifyCartItemRedirectToLoginIfUnauthorized() {
        webTestClient.get()
                .uri("/modify/items/" + 1L + "?action=plus")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().value("Location", Matchers.equalTo("/login"));
    }

    @Test
    @WithMockUser(username = "user1", roles = {"CUSTOMER"})
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
    void modifyCartRedirectToLoginIfUnauthorized() {
        webTestClient.get()
                .uri("/modify/cart/items/" + 1L + "?action=plus")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().value("Location", Matchers.equalTo("/login"));
    }

    @Test
    @WithMockUser(username = "user1", roles = {"CUSTOMER"})
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
    void fetchCartItemsRedirectToLoginIfUnauthorized() {
        webTestClient.get()
                .uri("/cart/items")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().value("Location", Matchers.equalTo("/login"));
    }

    @Test
    @WithMockUser(username = "user1", roles = {"CUSTOMER"})
    void fetchCartItems() {
        when(balanceApiClient.balanceGet(anyLong())).thenReturn(Mono.just(new BalanceGet200Response().balance(100L).userId(1L)));
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

    @Test
    void fetchCartItemsWorksIfPaymentsUnavailableRedirectToLoginIfUnauthorized() {
        webTestClient.get()
                .uri("/cart/items")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().value("Location", Matchers.equalTo("/login"));
    }

    @Test
    @WithMockUser(username = "user1", roles = {"CUSTOMER"})
    void fetchCartItemsWorksIfPaymentsUnavailable() {
        when(balanceApiClient.balanceGet(anyLong())).thenReturn(Mono.error(RuntimeException::new));
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
