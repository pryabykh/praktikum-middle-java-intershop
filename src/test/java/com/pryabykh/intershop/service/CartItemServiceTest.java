package com.pryabykh.intershop.service;

import com.pryabykh.intershop.constant.CartActions;
import com.pryabykh.intershop.dto.CartDto;
import com.pryabykh.intershop.entity.CartItem;
import com.pryabykh.intershop.entity.Item;
import com.pryabykh.intershop.repository.CartItemRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {CartItemServiceImpl.class})
public class CartItemServiceTest {
    @Autowired
    private CartItemService cartItemService;

    @MockitoBean
    private CartItemRepository cartItemRepository;

    @MockitoBean
    private UserService userService;

    @Captor
    private ArgumentCaptor<CartItem> cartItemArgumentCaptor;

    @BeforeEach
    void setUp() {
        Mockito.reset(userService, cartItemRepository);
    }

    @Test
    void modifyCart_whenPlus_cartCountShouldBeAdded() {
        when(userService.fetchDefaultUserId()).thenReturn(1L);
        CartItem cartItem = new CartItem();
        cartItem.setUserId(1L);
        cartItem.setCount(1);
        when(cartItemRepository.findByItemIdAndUserId(any(), any())).thenReturn(Optional.of(cartItem));

        cartItemService.modifyCart(1L, CartActions.PLUS);
        verify(cartItemRepository, times(1)).save(cartItemArgumentCaptor.capture());

        Assertions.assertEquals(2, cartItemArgumentCaptor.getValue().getCount());
    }

    @Test
    void modifyCart_whenMinus_cartCountShouldBeSubtracted() {
        when(userService.fetchDefaultUserId()).thenReturn(1L);
        CartItem cartItem = new CartItem();
        cartItem.setUserId(1L);
        cartItem.setCount(2);
        when(cartItemRepository.findByItemIdAndUserId(any(), any())).thenReturn(Optional.of(cartItem));

        cartItemService.modifyCart(1L, CartActions.MINUS);
        verify(cartItemRepository, times(1)).save(cartItemArgumentCaptor.capture());

        Assertions.assertEquals(1, cartItemArgumentCaptor.getValue().getCount());
    }

    @Test
    void modifyCart_whenDelete_cartItemShouldBeDeleted() {
        when(userService.fetchDefaultUserId()).thenReturn(1L);
        CartItem cartItem = new CartItem();
        cartItem.setUserId(1L);
        cartItem.setCount(2);
        when(cartItemRepository.findByItemIdAndUserId(any(), any())).thenReturn(Optional.of(cartItem));

        cartItemService.modifyCart(1L, CartActions.DELETE);
        verify(cartItemRepository, times(1)).delete(cartItem);
    }

    @Test
    void fetchCartItems_whenCartIsNotEmpty_shouldReturnItems() {
        when(userService.fetchDefaultUserId()).thenReturn(1L);

        Item item = new Item();
        item.setId(1L);
        item.setImageId(11L);
        item.setPrice(100L);
        item.setTitle("title");
        item.setDescription("description");

        CartItem cartItem = new CartItem();
        cartItem.setItem(item);
        cartItem.setCount(2);
        cartItem.setUserId(1L);
        cartItem.setId(1L);

        when(cartItemRepository.findByUserIdOrderByIdDesc(eq(1L))).thenReturn(List.of(cartItem));

        CartDto cartDto = cartItemService.fetchCartItems();

        assertNotNull(cartDto);
        assertFalse(cartDto.isEmpty());
        assertFalse(cartDto.getItems().isEmpty());
        assertEquals(1, cartDto.getItems().size());
        assertEquals(2L, cartDto.getTotal());
        assertEquals(1L, cartDto.getItems().get(0).getId());
        assertEquals("11", cartDto.getItems().get(0).getImgPath());
        assertEquals("2", cartDto.getItems().get(0).getPrice());
        assertEquals("title", cartDto.getItems().get(0).getTitle());
        assertEquals("description", cartDto.getItems().get(0).getDescription());
        assertEquals(2, cartDto.getItems().get(0).getCount());
    }
}
