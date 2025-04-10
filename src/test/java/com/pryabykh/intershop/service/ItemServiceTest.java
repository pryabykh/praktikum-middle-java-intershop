package com.pryabykh.intershop.service;

import com.pryabykh.intershop.dto.CreateItemDto;
import com.pryabykh.intershop.dto.ItemDto;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
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

        PageRequest pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.desc("id")));

        when(itemRepository.findAll(any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(item), pageable, 1));
        when(userService.fetchDefaultUserId()).thenReturn(1L);
        when(cartItemRepository.findByItemIdInAndUserId(any(), any())).thenReturn(new ArrayList<>());

        itemService.findAll(null, SortType.NO, 10, 0);

        verify(itemRepository, times(1)).findAll(eq(pageable));
        verify(cartItemRepository, times(1)).findByItemIdInAndUserId(eq(List.of(1L)), eq(1L));
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

        when(itemRepository.findAllByNameLike(eq("name1"), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(item), pageable, 1));

        itemService.findAll("name1", SortType.NO, 10, 0);

        verify(itemRepository, times(1)).findAllByNameLike(eq("name1"), eq(pageable));
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

        when(itemRepository.findAllByNameLike(eq("name1"), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(item), pageable, 1));

        itemService.findAll("name1", SortType.PRICE, 10, 0);

        verify(itemRepository, times(1)).findAllByNameLike(eq("name1"), eq(pageable));
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

        when(itemRepository.findAllByNameLike(eq("name1"), any(PageRequest.class)))
                .thenReturn(new PageImpl<>(List.of(item), pageable, 1));

        itemService.findAll("name1", SortType.ALPHA, 10, 0);

        verify(itemRepository, times(1)).findAllByNameLike(eq("name1"), eq(pageable));
    }

    @Test
    void findById_whenEntityExists_shouldReturnIt() {
        Item item = new Item();
        item.setId(1L);
        item.setImageId(11L);
        item.setPrice(111L);
        item.setTitle("title");
        item.setDescription("description");

        when(itemRepository.findById(eq(1L)))
                .thenReturn(Optional.of(item));

        ItemDto itemDto = itemService.findById(1L);

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
        when(itemRepository.save(any())).thenReturn(itemEntity);

        when(imagesService.upload(anyString())).thenReturn(1L);

        CreateItemDto item = new CreateItemDto();
        item.setBase64Image("base64Image");
        item.setPriceRubles(100L);
        item.setTitle("title");

        Long itemId = itemService.createItem(item);

        assertNotNull(item);
    }
}
