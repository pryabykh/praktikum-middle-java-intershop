package com.pryabykh.intershop.service;

import com.pryabykh.intershop.dto.ItemListDto;
import com.pryabykh.intershop.dto.ItemsPage;
import com.pryabykh.intershop.enums.SortType;
import com.pryabykh.intershop.repository.ItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    public ItemServiceImpl(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public ItemsPage findAll(String name, SortType sort,  int pageSize, int pageNumber) {
        PageRequest pageRequest = createPageRequest(sort, pageSize, pageNumber);
        Page<ItemListDto> items = Optional.ofNullable(name)
                .map(n -> itemRepository.findAllByNameLike(n, pageRequest))
                .orElse(itemRepository.findAll(pageRequest))
                .map(item -> {
                    return new ItemListDto(
                            item.getId(),
                            item.getTitle(),
                            String.valueOf(item.getPrice() / 100),
                            item.getDescription(),
                            "/images/" + item.getImageId(),
                            0
                    );
                });
        return new ItemsPage(items);
    }

    private PageRequest createPageRequest(SortType sortType,  int pageSize, int pageNumber) {
        Sort sort;
        if (SortType.ALPHA.equals(sortType)) {
            sort = Sort.by("title");
        } else if (SortType.PRICE.equals(sortType)) {
            sort = Sort.by("price");
        } else {
            sort = Sort.by(Sort.Order.desc("id"));
        }
        return PageRequest.of(pageNumber, pageSize, sort);
    }
}
