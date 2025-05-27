package com.pryabykh.intershop.controller;

import com.pryabykh.intershop.entity.Image;
import com.pryabykh.intershop.entity.Item;
import com.pryabykh.intershop.entity.User;
import com.pryabykh.intershop.repository.ImageRepository;
import com.pryabykh.intershop.repository.ItemRepository;
import com.pryabykh.intershop.service.CacheService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.web.reactive.function.BodyInserters;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class ItemControllerIntegrationTest extends WebFluxPostgreSQLTestContainerBaseTest {

    @Autowired
    @MockitoSpyBean
    private ItemRepository itemRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private DatabaseClient databaseClient;

    @Autowired
    private CacheService cacheService;

    @BeforeEach
    void setUp() {
        cacheService.evictAllCaches().block();
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
    void root() throws Exception {
        webTestClient.get()
                .uri("/")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().value("Location", Matchers.equalTo("/main/items"));
    }

    @Test
    void mainItems() throws Exception {
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

        User user = new User("test");

        webTestClient.get()
                .uri("/main/items")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody();

        webTestClient.get()
                .uri("/main/items")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody();

        verify(itemRepository, times(1)).findAllOrderByIdDesc(anyLong(), anyInt(), anyInt());
    }

    @Test
    void fetchItem() throws Exception {
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

        webTestClient.get()
                .uri("/items/" + savedItem.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody();

        webTestClient.get()
                .uri("/items/" + savedItem.getId())
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody();

        verify(itemRepository, times(1)).findItemById(anyLong(), anyLong());
    }

    @Test
    void showCreateItemFormRedirectToLoginIfUnauthorized() {
        webTestClient.get()
                .uri("/create-item-form")
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().value("Location", Matchers.equalTo("/login"));
    }

    @Test
    @WithMockUser(username = "user1", roles = {"CUSTOMER"})
    void showCreateItemFormReturnsForbiddenIfHasWrongRole() {
        webTestClient.get()
                .uri("/create-item-form")
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    @WithMockUser(username = "ADMIN", roles = {"ADMIN"})
    void showCreateItemForm() throws Exception {
        webTestClient.get()
                .uri("/create-item-form")
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType("text/html")
                .expectBody();
    }

    @Test
    void createItemRedirectToLoginIfUnauthorized() {
        webTestClient.post()
                .uri("/create-item")
                .body(BodyInserters.fromFormData("title", "test")
                        .with("description", "test")
                        .with("priceRubles", "1111")
                        .with("base64Image", "test"))
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().value("Location", Matchers.equalTo("/login"));
    }

    @Test
    @WithMockUser(username = "user1", roles = {"CUSTOMER"})
    void createItemReturnsForbiddenIfHasWrongRole() {
        webTestClient.post()
                .uri("/create-item")
                .body(BodyInserters.fromFormData("title", "test")
                        .with("description", "test")
                        .with("priceRubles", "1111")
                        .with("base64Image", "test"))
                .exchange()
                .expectStatus().isForbidden();
    }

    @Test
    @WithMockUser(username = "ADMIN", roles = {"ADMIN"})
    void createItem() throws Exception {
        webTestClient.post()
                .uri("/create-item")
                .body(BodyInserters.fromFormData("title", "test")
                        .with("description", "test")
                        .with("priceRubles", "1111")
                        .with("base64Image", "test"))
                .exchange()
                .expectStatus().is3xxRedirection()
                .expectHeader().value("Location", Matchers.equalTo("/"));
    }
}
