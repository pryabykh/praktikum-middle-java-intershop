package com.pryabykh.intershop.service;

import com.pryabykh.intershop.dto.ItemListDto;
import com.pryabykh.intershop.dto.ItemsPage;
import com.pryabykh.intershop.entity.CartItem;
import com.pryabykh.intershop.enums.SortType;
import com.pryabykh.intershop.repository.CartItemRepository;
import com.pryabykh.intershop.repository.ItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final CartItemRepository cartItemRepository;
    private final UserService userService;
    public ItemServiceImpl(ItemRepository itemRepository,
                           CartItemRepository cartItemRepository,
                           UserService userService) {
        this.itemRepository = itemRepository;
        this.cartItemRepository = cartItemRepository;
        this.userService = userService;
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
        assignCountToItems(items);
        return new ItemsPage(items);
    }

    private void assignCountToItems(Page<ItemListDto> items) {
        Map<Long, Integer> itemCountByItemId = cartItemRepository.findByItemIdInAndUserId(
                items.stream().map(ItemListDto::getId).distinct().collect(Collectors.toList()),
                userService.fetchDefaultUserId()
        ).stream().collect(Collectors.toMap(cartItem -> cartItem.getItem().getId(), CartItem::getCount));
        for (ItemListDto item : items) {
            Integer count = itemCountByItemId.get(item.getId());
            if (count != null) {
                item.setCount(count);
            } else {
                item.setCount(0);
            }
        }
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
