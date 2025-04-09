package com.pryabykh.intershop.service;

import com.pryabykh.intershop.entity.CartItem;
import com.pryabykh.intershop.entity.Item;
import com.pryabykh.intershop.repository.CartItemRepository;
import com.pryabykh.intershop.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = {OrderServiceImpl.class})
public class OrderServiceTest {
    @Autowired
    private OrderService orderService;

    @MockitoBean
    private OrderRepository orderRepository;

    @MockitoBean
    private CartItemRepository cartItemRepository;

    @MockitoBean
    private UserService userService;

    @BeforeEach
    void setUp() {
        Mockito.reset(userService, cartItemRepository, orderRepository);
    }

    @Test
    void createOrder_whenItemsExistInCart_ShouldCreateOrder() {
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

        when(cartItemRepository.findByUserId(eq(1L))).thenReturn(List.of(cartItem));

        orderService.createOrder();

        verify(orderRepository, times(1)).save(any());
        verify(cartItemRepository, times(1)).deleteByUserId(eq(1L));
    }
}
