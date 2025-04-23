package com.pryabykh.intershop.service;

import com.pryabykh.intershop.dto.CreateItemDto;
import com.pryabykh.intershop.dto.ItemDto;
import com.pryabykh.intershop.dto.ItemsPage;
import com.pryabykh.intershop.entity.Item;
import com.pryabykh.intershop.enums.SortType;
import com.pryabykh.intershop.repository.CartItemRepository;
import com.pryabykh.intershop.repository.ItemRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {ItemServiceImpl.class})
public class ItemServiceTest {
    @Autowired
    private ItemService itemService;

    @MockitoBean
    private ItemRepository itemRepository;

    @MockitoBean
    private CartItemRepository cartItemRepository;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private ImagesService imagesService;

    @BeforeEach
    void setUp() {
        Mockito.reset(itemRepository, userService, cartItemRepository, imagesService);
    }

    @Test
    void findAll_whenNameIsNotProvided_shouldNotApplyFilters() {
        Item item = new Item();
        item.setId(1L);
        item.setImageId(11L);
        item.setPrice(111L);
        item.setTitle("title");
        item.setDescription("description");

        when(itemRepository.findAllOrderByIdDesc(1L, 10, 0))
                .thenReturn(Flux.just(item));
        when(itemRepository.countByNameLike(anyString()))
                .thenReturn(Mono.just(1L));
        when(itemRepository.count())
                .thenReturn(Mono.just(1L));
        when(userService.fetchDefaultUserId()).thenReturn(Mono.just(1L));

        ItemsPage block = itemService.findAll(null, SortType.NO, 10, 0).block();
        assertNotNull(block);
    }

    @Test
    void findAll_whenNameProvided_shouldApplyFilters() {
        Item item = new Item();
        item.setId(1L);
        item.setImageId(11L);
        item.setPrice(111L);
        item.setTitle("title");
        item.setDescription("description");

        PageRequest pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("id")));

        when(userService.fetchDefaultUserId()).thenReturn(Mono.just(1L));
        when(itemRepository.findAllByNameLikeOrderByIdDesc(eq(1L), eq("name1"), eq(10), eq(0)))
                .thenReturn(Flux.just(item));
        when(itemRepository.countByNameLike(eq("name1")))
                .thenReturn(Mono.just(1L));

        ItemsPage block = itemService.findAll("name1", SortType.NO, 10, 0).block();
        assertNotNull(block);
    }

    @Test
    void findAll_whenSortPriceProvided_shouldApplyPriceSort() {
        Item item = new Item();
        item.setId(1L);
        item.setImageId(11L);
        item.setPrice(111L);
        item.setTitle("title");
        item.setDescription("description");

        PageRequest pageable = PageRequest.of(0, 10, Sort.by("price"));

        when(userService.fetchDefaultUserId()).thenReturn(Mono.just(1L));
        when(itemRepository.findAllByNameLikeOrderByPriceAsc(eq(1L), eq("name1"), eq(10), eq(0)))
                .thenReturn(Flux.just(item));
        when(itemRepository.countByNameLike(eq("name1")))
                .thenReturn(Mono.just(1L));

        ItemsPage block = itemService.findAll("name1", SortType.PRICE, 10, 0).block();
        assertNotNull(block);
    }

    @Test
    void findAll_whenSortAlphaProvided_shouldApplyAlphaSort() {
        Item item = new Item();
        item.setId(1L);
        item.setImageId(11L);
        item.setPrice(111L);
        item.setTitle("title");
        item.setDescription("description");

        PageRequest pageable = PageRequest.of(0, 10, Sort.by("title"));

        when(userService.fetchDefaultUserId()).thenReturn(Mono.just(1L));
        when(itemRepository.findAllByNameLikeOrderByTitleAsc(eq(1L), eq("name1"), eq(10), eq(0)))
                .thenReturn(Flux.just(item));
        when(itemRepository.countByNameLike(eq("name1")))
                .thenReturn(Mono.just(1L));

        ItemsPage block = itemService.findAll("name1", SortType.ALPHA, 10, 0).block();
        assertNotNull(block);
    }

    @Test
    void findById_whenEntityExists_shouldReturnIt() {
        Item item = new Item();
        item.setId(1L);
        item.setImageId(11L);
        item.setPrice(111L);
        item.setTitle("title");
        item.setDescription("description");

        when(userService.fetchDefaultUserId()).thenReturn(Mono.just(1L));
        when(itemRepository.findItemById(eq(1L), eq(1L)))
                .thenReturn(Mono.just(item));

        ItemDto itemDto = itemService.findById(1L).block();

        assertNotNull(itemDto);
        assertEquals(1L, itemDto.getId());
        assertEquals("11", itemDto.getImgPath());
        assertEquals(String.valueOf(111L / 100L), itemDto.getPrice());
        assertEquals("title", itemDto.getTitle());
        assertEquals("description", itemDto.getDescription());
    }

    @Test
    void createItem_whenValidDataProvided_shouldCreateNewItem() {
        Item itemEntity = new Item();
        itemEntity.setId(1L);
        when(itemRepository.save(any())).thenReturn(Mono.just(itemEntity));

        when(imagesService.upload(anyString())).thenReturn(Mono.just(1L));

        CreateItemDto item = new CreateItemDto();
        item.setBase64Image("base64Image");
        item.setPriceRubles(100L);
        item.setTitle("title");

        Long itemId = itemService.createItem(item).block();

        assertNotNull(item);
    }
}
