package com.pryabykh.intershop.service;

import com.pryabykh.intershop.dto.ItemsPage;
import com.pryabykh.intershop.enums.SortType;
import org.springframework.data.domain.Pageable;

public interface ItemService {

    ItemsPage findAll(String name, SortType sort, int pageSize, int pageNumber);
}
