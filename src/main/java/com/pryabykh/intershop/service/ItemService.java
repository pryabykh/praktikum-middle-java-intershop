package com.pryabykh.intershop.service;

import com.pryabykh.intershop.dto.CreateItemDto;
import com.pryabykh.intershop.dto.ItemDto;
import com.pryabykh.intershop.dto.ItemsPage;
import com.pryabykh.intershop.enums.SortType;

public interface ItemService {

    ItemsPage findAll(String name, SortType sort, int pageSize, int pageNumber);

    ItemDto findById(Long id);

    Long createItem(CreateItemDto itemDto);
}
