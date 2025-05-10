package com.pryabykh.intershop.service;

import com.pryabykh.intershop.client.PaymentApiClient;
import com.pryabykh.intershop.client.domain.PayPost200Response;
import com.pryabykh.intershop.dto.OrderDto;
import com.pryabykh.intershop.entity.CartItem;
import com.pryabykh.intershop.entity.Item;
import com.pryabykh.intershop.entity.Order;
import com.pryabykh.intershop.entity.OrderItem;
import com.pryabykh.intershop.repository.CartItemRepository;
import com.pryabykh.intershop.repository.ItemRepository;
import com.pryabykh.intershop.repository.OrderItemRepository;
import com.pryabykh.intershop.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
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

    @MockitoBean
    private ItemRepository itemRepository;

    @MockitoBean
    private OrderItemRepository orderItemRepository;

    @MockitoBean
    private PaymentApiClient paymentApiClient;

    @MockitoBean
    private CacheService cacheService;

    @BeforeEach
    void setUp() {
        Mockito.reset(userService, cartItemRepository, orderRepository, itemRepository, orderItemRepository, cacheService);
    }

    @Test
    void createOrder_whenItemsExistInCart_ShouldCreateOrder() {
        when(cacheService.evictCaches(anyLong(), eq(null))).thenReturn(Mono.empty());
        when(cacheService.evictAllCaches()).thenReturn(Mono.empty());
        when(paymentApiClient.payPost(any()))
                .thenReturn(Mono.just(new PayPost200Response().newBalance(1L).message("success").success(true)));
        when(userService.fetchDefaultUserId()).thenReturn(Mono.just(1L));
        when(cartItemRepository.deleteByUserId(eq(1L))).thenReturn(Mono.empty().then());
        Item item = new Item();
        item.setId(1L);
        item.setImageId(11L);
        item.setPrice(100L);
        item.setTitle("title");
        item.setDescription("description");
        when(itemRepository.findById(eq(1L))).thenReturn(Mono.just(item));

        CartItem cartItem = new CartItem();
        cartItem.setItemId(item.getId());
        cartItem.setCount(2);
        cartItem.setUserId(1L);
        cartItem.setId(1L);

        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);

        when(cartItemRepository.findByUserIdOrderByIdDesc(eq(1L))).thenReturn(Flux.just(cartItem));
        Order savedOrder = new Order();
        savedOrder.setId(1L);
        savedOrder.setUserId(1L);
        when(orderRepository.save(any())).thenReturn(Mono.just(savedOrder));
        when(orderItemRepository.save(any())).thenReturn(Mono.just(orderItem));

        Long orderId = orderService.createOrder().block();

        assertNotNull(orderId);
        verify(orderRepository, times(1)).save(any());
        verify(cartItemRepository, times(1)).deleteByUserId(eq(1L));
    }

    @Test
    void findById_whenEntityExists_shouldReturnIt() {
        when(paymentApiClient.payPost(any()))
                .thenReturn(Mono.just(new PayPost200Response().newBalance(1L).message("success").success(true)));
        Order order = new Order();
        order.setId(1L);
        order.setUserId(1L);
        order.setTotalSum(100L);

        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setPrice(100L);
        orderItem.setOrderId(order.getId());
        orderItem.setTitle("title");
        orderItem.setDescription("desc");
        orderItem.setImageId(11L);
        orderItem.setCount(2);

        when(orderRepository.findById(eq(1L)))
                .thenReturn(Mono.just(order));
        when(orderItemRepository.findByOrderId(eq(1L)))
                .thenReturn(Flux.just(orderItem));

        OrderDto orderDto = orderService.findById(1L).block();

        assertNotNull(orderDto);
        assertEquals(1L, orderDto.getId());
        assertEquals(1L, orderDto.getTotalSum());
        assertEquals("11", orderDto.getItems().get(0).getImgPath());
        assertEquals("title", orderDto.getItems().get(0).getTitle());
        assertEquals(2, orderDto.getItems().get(0).getCount());
    }

    @Test
    void findAll_whenEntityExists_shouldReturnIt() {
        when(paymentApiClient.payPost(any()))
                .thenReturn(Mono.just(new PayPost200Response().newBalance(1L).message("success").success(true)));
        when(userService.fetchDefaultUserId()).thenReturn(Mono.just(1L));
        Order order = new Order();
        order.setId(1L);
        order.setUserId(1L);
        order.setTotalSum(100L);

        OrderItem orderItem = new OrderItem();
        orderItem.setId(1L);
        orderItem.setPrice(100L);
        orderItem.setOrderId(order.getId());
        orderItem.setTitle("title");
        orderItem.setDescription("desc");
        orderItem.setImageId(11L);
        orderItem.setCount(2);

        when(orderRepository.findByUserIdOrderByIdDesc(eq(1L)))
                .thenReturn(Flux.just(order));
        when(orderItemRepository.findByOrderId(eq(1L)))
                .thenReturn(Flux.just(orderItem));

        OrderDto o = orderService.findAll().blockFirst();

        assertNotNull(o);
        assertEquals(1L, o.getId());
        assertEquals(1L, o.getTotalSum());
        assertEquals("11", o.getItems().get(0).getImgPath());
        assertEquals("title", o.getItems().get(0).getTitle());
        assertEquals(2, o.getItems().get(0).getCount());
    }
}
