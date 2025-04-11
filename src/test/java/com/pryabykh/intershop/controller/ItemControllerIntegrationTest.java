package com.pryabykh.intershop.controller;

import com.pryabykh.intershop.entity.Image;
import com.pryabykh.intershop.entity.Item;
import com.pryabykh.intershop.repository.ImageRepository;
import com.pryabykh.intershop.repository.ItemRepository;
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

public class ItemControllerIntegrationTest extends MockMvcPostgreSQLTestContainerBaseTest {

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private ImageRepository imageRepository;


    @Test
    @Transactional
    void root() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/main/items"));
    }

    @Test
    @Transactional
    void mainItems() throws Exception {
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

        mockMvc.perform(get("/main/items"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("main"))
                .andExpect(model().attributeExists("paging"))
                .andExpect(model().attributeExists("items"))
                .andExpect(model().attributeExists("sort"));
    }

    @Test
    @Transactional
    void fetchItem() throws Exception {
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

        mockMvc.perform(get("/items/" + savedItem.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("item"))
                .andExpect(model().attributeExists("item"));
    }

    @Test
    @Transactional
    void showCreateItemForm() throws Exception {
        mockMvc.perform(get("/create-item-form"))
                .andExpect(status().isOk())
                .andExpect(content().contentType("text/html;charset=UTF-8"))
                .andExpect(view().name("create-item"));
    }

    @Test
    @Transactional
    void createItem() throws Exception {
        mockMvc.perform(post("/create-item")
                        .param("title", "test")
                        .param("description", "test")
                        .param("priceRubles", "1111")
                        .param("base64Image", "test"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/"));
    }
}
