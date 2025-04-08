package com.pryabykh.intershop.repository;

import com.pryabykh.intershop.entity.CartItem;
import com.pryabykh.intershop.entity.Item;
import com.pryabykh.intershop.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.jdbc.Sql;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CartItemRepositoryTest extends DataJpaPostgreSQLTestContainerBaseTest {

    @Autowired
    private CartItemRepository cartItemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;

    @Test
    @Sql(statements = {
            "insert into intershop.users (name) values ('ADMIN');",
            "insert into intershop.images (name, bytes) values ('small_image.png', decode('74657374', 'hex'));",
            "insert into intershop.items (title, price, description, image_id) values ('IPhone 1', 100, 'IPhone 1', (select id from intershop.images order by id limit 1));",
            "insert into intershop.carts (user_id, item_id, count) values ((select id from intershop.users order by id limit 1), (select id from intershop.items order by id limit 1), 1);",
    })
    void findByItemIdAndUserId_whenEntityExists_shouldReturnIt() {
        Long userId = userRepository.findAll().stream().findFirst().map(User::getId).orElseThrow();
        Long itemId = itemRepository.findAll().stream().findFirst().map(Item::getId).orElseThrow();
        Optional<CartItem> cartItem = cartItemRepository.findByItemIdAndUserId(itemId, userId);
        assertNotNull(cartItem);
        assertTrue(cartItem.isPresent());
    }

    @Test
    @Sql(statements = {
            "insert into intershop.users (name) values ('ADMIN');",
            "insert into intershop.images (name, bytes) values ('small_image.png', decode('74657374', 'hex'));",
            "insert into intershop.items (title, price, description, image_id) values ('IPhone 1', 100, 'IPhone 1', (select id from intershop.images order by id limit 1));",
            "insert into intershop.carts (user_id, item_id, count) values ((select id from intershop.users order by id limit 1), (select id from intershop.items order by id limit 1), 1);",
    })
    void findByItemIdInAndUserId_whenEntityExists_shouldReturnIt() {
        Long userId = userRepository.findAll().stream().findFirst().map(User::getId).orElseThrow();
        Long itemId = itemRepository.findAll().stream().findFirst().map(Item::getId).orElseThrow();
        List<CartItem> cartItems = cartItemRepository.findByItemIdInAndUserId(List.of(itemId), userId);
        assertNotNull(cartItems);
        assertFalse(cartItems.isEmpty());
        assertEquals(1, cartItems.size());
    }
}
