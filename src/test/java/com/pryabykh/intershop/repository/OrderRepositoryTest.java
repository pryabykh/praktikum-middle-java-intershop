package com.pryabykh.intershop.repository;

import com.pryabykh.intershop.entity.Order;
import com.pryabykh.intershop.entity.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class OrderRepositoryTest extends DataJpaPostgreSQLTestContainerBaseTest {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private UserRepository userRepository;

    @Test
    @Sql(statements = {
            "insert into intershop.items (title, price, description, image_id) values ('Samsung 10', 100, 'Samsung 10', (select id from intershop.images order by id limit 1));",
            "insert into intershop.orders (total_sum, user_id) values (100, (select id from intershop.users order by id limit 1));",
    })
    void findByUserIdOrderByIdDesc_whenEntityExists_shouldReturnIt() {
        Long userId = userRepository.findAll().stream().findFirst().map(User::getId).orElseThrow();
        List<Order> orders = orderRepository.findByUserIdOrderByIdDesc(userId);
        assertNotNull(orders);
        assertFalse(orders.isEmpty());
        assertEquals(1, orders.size());
    }

}
