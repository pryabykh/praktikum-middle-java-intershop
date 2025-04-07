package com.pryabykh.intershop.repository;

import com.pryabykh.intershop.entity.Item;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.test.context.jdbc.Sql;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class ItemRepositoryTest extends DataJpaPostgreSQLTestContainerBaseTest {

    @Autowired
    private ItemRepository itemRepository;

    @Test
    @Sql(statements = {
            "insert into intershop.images (name, bytes) values ('small_image.png', decode('74657374', 'hex'));",
            "insert into intershop.items (title, price, description, image_id) values ('IPhone 1', 100, 'IPhone 1', (select id from intershop.images order by id limit 1));",
            "insert into intershop.items (title, price, description, image_id) values ('IPhone 2', 100, 'IPhone 2', (select id from intershop.images order by id limit 1));",
            "insert into intershop.items (title, price, description, image_id) values ('IPhone 3', 100, 'IPhone 3', (select id from intershop.images order by id limit 1));",
            "insert into intershop.items (title, price, description, image_id) values ('Samsung 10', 100, 'Samsung 10', (select id from intershop.images order by id limit 1));",
    })
    void findAllByNameLike_whenTableHasRowsWithFetchedName_shouldReturnRows() {
        Page<Item> items = itemRepository.findAllByNameLike("iphone", PageRequest.of(0, 10, Sort.by("id")));
        assertNotNull(items);
        assertEquals(3, items.getTotalElements());
    }

    @Test
    void findAllByNameLike_whenTableDoesNotHaveRowsWithFetchedName_shouldReturnEmptyResult() {
        Page<Item> items = itemRepository.findAllByNameLike("iphone", PageRequest.of(0, 10, Sort.by("id")));
        assertNotNull(items);
        assertEquals(0, items.getTotalElements());
    }
}
