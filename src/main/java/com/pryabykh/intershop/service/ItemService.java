package com.pryabykh.intershop.service;

import com.pryabykh.intershop.dto.CreateItemDto;
import com.pryabykh.intershop.dto.ItemDto;
import com.pryabykh.intershop.dto.ItemsPage;
import com.pryabykh.intershop.enums.SortType;
import reactor.core.publisher.Mono;

public interface ItemService {

    Mono<ItemsPage> findAll(String name, SortType sortType, int pageSize, int pageNumber);

    Mono<ItemDto> findById(Long id);

    Mono<Long> createItem(CreateItemDto itemDto);
}
