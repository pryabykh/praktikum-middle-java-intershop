package com.pryabykh.payment.service;

import com.pryabykh.payment.dto.CreateItemDto;
import com.pryabykh.payment.dto.ItemDto;
import com.pryabykh.payment.dto.ItemsPage;
import com.pryabykh.payment.enums.SortType;
import reactor.core.publisher.Mono;

public interface ItemService {

    Mono<ItemsPage> findAll(String name, SortType sortType, int pageSize, int pageNumber);

    Mono<ItemDto> findById(Long id);

    Mono<Long> createItem(CreateItemDto itemDto);
}
