package com.pryabykh.intershop.service;

import com.pryabykh.intershop.dto.ItemDto;
import com.pryabykh.intershop.dto.OrderDto;
import com.pryabykh.intershop.entity.CartItem;
import com.pryabykh.intershop.entity.Item;
import com.pryabykh.intershop.entity.Order;
import com.pryabykh.intershop.entity.OrderItem;
import com.pryabykh.intershop.repository.CartItemRepository;
import com.pryabykh.intershop.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
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

        when(cartItemRepository.findByUserIdOrderByIdDesc(eq(1L))).thenReturn(List.of(cartItem));
        Order savedOrder = new Order();
        savedOrder.setId(1L);
        when(orderRepository.save(any())).thenReturn(savedOrder);

        Long orderId = orderService.createOrder();

        assertNotNull(orderId);
        verify(orderRepository, times(1)).save(any());
        verify(cartItemRepository, times(1)).deleteByUserId(eq(1L));
    }

    @Test
    void findById_whenEntityExists_shouldReturnIt() {
        Order order = new Order();
        order.setId(1L);
        order.setUserId(1L);
        order.setTotalSum(100L);

        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setPrice(100L);
        orderItem.setOrder(order);
        orderItem.setTitle("title");
        orderItem.setDescription("desc");
        orderItem.setImageId(11L);
        orderItem.setCount(2);

        order.setOrderItems(List.of(orderItem));

        when(orderRepository.findById(eq(1L)))
                .thenReturn(Optional.of(order));

        OrderDto orderDto = orderService.findById(1L);

        assertNotNull(orderDto);
        assertEquals(1L, orderDto.getId());
        assertEquals(1L, orderDto.getTotalSum());
        assertEquals("11", orderDto.getItems().get(0).getImgPath());
        assertEquals("title", orderDto.getItems().get(0).getTitle());
        assertEquals(2, orderDto.getItems().get(0).getCount());
    }
}
