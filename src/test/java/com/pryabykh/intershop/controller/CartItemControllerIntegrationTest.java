package com.pryabykh.intershop.controller;

import com.pryabykh.intershop.entity.CartItem;
import com.pryabykh.intershop.entity.Image;
import com.pryabykh.intershop.entity.Item;
import com.pryabykh.intershop.entity.User;
import com.pryabykh.intershop.repository.CartItemRepository;
import com.pryabykh.intershop.repository.ImageRepository;
import com.pryabykh.intershop.repository.ItemRepository;
import com.pryabykh.intershop.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

public class CartItemControllerIntegrationTest extends MockMvcPostgreSQLTestContainerBaseTest {

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @Transactional
    void modifyCartAndRedirectToMain() throws Exception {
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
        cartItem.setUserId(userRepository.save(new User("admin")).getId());
        cartItemRepository.save(cartItem);

        mockMvc.perform(post("/main/items/" + item.getId()).param("action", "plus"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/main/items"));
    }

    @Test
    @Transactional
    void modifyCartAndRedirectToItem() throws Exception {
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
        cartItem.setUserId(userRepository.save(new User("admin")).getId());
        cartItemRepository.save(cartItem);

        mockMvc.perform(post("/items/" + item.getId()).param("action", "plus"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/items/" + item.getId()));
    }

    @Test
    @Transactional
    void modifyCartAndRedirectToCart() throws Exception {
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
        cartItem.setUserId(userRepository.save(new User("admin")).getId());
        cartItemRepository.save(cartItem);

        mockMvc.perform(post("/cart/items/" + item.getId()).param("action", "plus"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/cart/items"));
    }

    @Test
    @Transactional
    void fetchCartItems() throws Exception {
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
        cartItem.setUserId(userRepository.save(new User("admin")).getId());
        cartItemRepository.save(cartItem);

        mockMvc.perform(get("/cart/items"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("cart"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("total"))
                .andExpect(model().attributeExists("empty"));
    }
}
