package com.pryabykh.intershop.service;

import com.pryabykh.intershop.dto.CreateItemDto;
import com.pryabykh.intershop.dto.ItemDto;
import com.pryabykh.intershop.dto.ItemsPage;
import com.pryabykh.intershop.entity.CartItem;
import com.pryabykh.intershop.entity.Item;
import com.pryabykh.intershop.enums.SortType;
import com.pryabykh.intershop.repository.CartItemRepository;
import com.pryabykh.intershop.repository.ItemRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final CartItemRepository cartItemRepository;
    private final UserService userService;
    private final ImagesService imagesService;
    public ItemServiceImpl(ItemRepository itemRepository,
                           CartItemRepository cartItemRepository,
                           UserService userService,
                           ImagesService imagesService) {
        this.itemRepository = itemRepository;
        this.cartItemRepository = cartItemRepository;
        this.userService = userService;
        this.imagesService = imagesService;
    }

    @Override
    @Transactional(readOnly = true)
    public ItemsPage findAll(String name, SortType sort,  int pageSize, int pageNumber) {
        PageRequest pageRequest = createPageRequest(sort, pageSize, pageNumber);
        Page<ItemDto> items = Optional.ofNullable(name)
                .map(n -> itemRepository.findAllByNameLike(n, pageRequest))
                .orElse(itemRepository.findAll(pageRequest))
                .map(item -> {
                    return new ItemDto(
                            item.getId(),
                            item.getTitle(),
                            String.valueOf(item.getPrice() / 100),
                            item.getDescription(),
                            String.valueOf(item.getImageId())
                    );
                });
        assignCountToItems(items.getContent());
        return new ItemsPage(items);
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto findById(Long id) {
        return itemRepository.findById(id).map(item -> {
            ItemDto itemDto = new ItemDto(
                    item.getId(),
                    item.getTitle(),
                    String.valueOf(item.getPrice() / 100),
                    item.getDescription(),
                    String.valueOf(item.getImageId())
            );
            assignCountToItems(List.of(itemDto));
            return itemDto;
        }).orElseThrow();
    }

    @Override
    @Transactional
    public Long createItem(CreateItemDto itemDto) {
        Item item = new Item();
        item.setTitle(itemDto.getTitle());
        item.setDescription(itemDto.getDescription());
        item.setPrice(itemDto.getPriceRubles() * 100);
        item.setImageId(imagesService.upload(itemDto.getBase64Image()));
        return itemRepository.save(item).getId();
    }

    private void assignCountToItems(List<ItemDto> items) {
        Map<Long, Integer> itemCountByItemId = cartItemRepository.findByItemIdInAndUserId(
                items.stream().map(ItemDto::getId).distinct().collect(Collectors.toList()),
                userService.fetchDefaultUserId()
        ).stream().collect(Collectors.toMap(cartItem -> cartItem.getItem().getId(), CartItem::getCount));
        for (ItemDto item : items) {
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
