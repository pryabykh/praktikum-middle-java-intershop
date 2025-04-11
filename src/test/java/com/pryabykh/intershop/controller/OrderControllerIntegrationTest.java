package com.pryabykh.intershop.controller;

import com.pryabykh.intershop.entity.CartItem;
import com.pryabykh.intershop.entity.Image;
import com.pryabykh.intershop.entity.Item;
import com.pryabykh.intershop.entity.Order;
import com.pryabykh.intershop.repository.CartItemRepository;
import com.pryabykh.intershop.repository.ImageRepository;
import com.pryabykh.intershop.repository.ItemRepository;
import com.pryabykh.intershop.repository.OrderRepository;
import com.pryabykh.intershop.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class OrderControllerIntegrationTest extends MockMvcPostgreSQLTestContainerBaseTest {

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

    @Test
    @Transactional
    void createOrder() throws Exception {
        Image image = new Image();
        image.setName("n");
        image.setBytes("b".getBytes(StandardCharsets.UTF_8));
        Long imageId = imageRepository.save(image).getId();

        Item item = new Item();
        item.setPrice(1L);
        item.setDescription("d");
        item.setImageId(imageId);
        item.setTitle("t");
        Item savedItem = itemRepository.save(item);

        CartItem cartItem = new CartItem();
        cartItem.setItem(savedItem);
        cartItem.setCount(1);
        cartItem.setUserId(userService.fetchDefaultUserId());
        cartItemRepository.save(cartItem);

        mockMvc.perform(post("/buy"))
                .andExpect(status().is3xxRedirection());
    }

    @Test
    @Transactional
    void fetchOrder() throws Exception {
        Image image = new Image();
        image.setName("n");
        image.setBytes("b".getBytes(StandardCharsets.UTF_8));
        Long imageId = imageRepository.save(image).getId();

        Item item = new Item();
        item.setPrice(1L);
        item.setDescription("d");
        item.setImageId(imageId);
        item.setTitle("t");
        Item savedItem = itemRepository.save(item);

        CartItem cartItem = new CartItem();
        cartItem.setItem(savedItem);
        cartItem.setCount(1);
        cartItem.setUserId(userService.fetchDefaultUserId());
        cartItemRepository.save(cartItem);

        mockMvc.perform(post("/buy"))
                .andExpect(status().is3xxRedirection());

        Long orderId = orderRepository.findAll().stream().findFirst().map(Order::getId).orElseThrow();

        mockMvc.perform(get("/orders/" + orderId))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("order"))
                .andExpect(model().attributeExists("newOrder"))
                .andExpect(model().attributeExists("order"));
    }

    @Test
    @Transactional
    void fetchOrders() throws Exception {
        Image image = new Image();
        image.setName("n");
        image.setBytes("b".getBytes(StandardCharsets.UTF_8));
        Long imageId = imageRepository.save(image).getId();

        Item item = new Item();
        item.setPrice(1L);
        item.setDescription("d");
        item.setImageId(imageId);
        item.setTitle("t");
        Item savedItem = itemRepository.save(item);

        CartItem cartItem = new CartItem();
        cartItem.setItem(savedItem);
        cartItem.setCount(1);
        cartItem.setUserId(userService.fetchDefaultUserId());
        cartItemRepository.save(cartItem);

        mockMvc.perform(post("/buy"))
                .andExpect(status().is3xxRedirection());

        Long orderId = orderRepository.findAll().stream().findFirst().map(Order::getId).orElseThrow();

        mockMvc.perform(get("/orders"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("orders"))
                .andExpect(model().attributeExists("orders"));
    }
}
