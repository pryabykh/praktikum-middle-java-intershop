package com.pryabykh.intershop.service;

import com.pryabykh.intershop.dto.CreateItemDto;
import com.pryabykh.intershop.dto.ItemDto;
import com.pryabykh.intershop.dto.ItemsPage;
import com.pryabykh.intershop.entity.Item;
import com.pryabykh.intershop.enums.SortType;
import com.pryabykh.intershop.repository.ItemRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@Service
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final ImagesService imagesService;
    private final UserService userService;
    private final CacheService cacheService;

    public ItemServiceImpl(ItemRepository itemRepository,
                           ImagesService imagesService,
                           UserService userService, CacheService cacheService) {
        this.itemRepository = itemRepository;
        this.imagesService = imagesService;
        this.userService = userService;
        this.cacheService = cacheService;
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ItemsPage> findAll(String name, SortType sortType, int pageSize, int pageNumber) {
        int offset = pageNumber * pageSize;

        Flux<ItemDto> itemsFlux = Optional.ofNullable(name)
                .map(n -> {
                    if (SortType.ALPHA.equals(sortType)) {
                        return userService.fetchDefaultUserId().flatMapMany(userId -> {
                            return itemRepository.findAllByNameLikeOrderByTitleAsc(userId, n, pageSize, offset);
                        });
                    } else if (SortType.PRICE.equals(sortType)) {
                        return userService.fetchDefaultUserId().flatMapMany(userId -> {
                            return itemRepository.findAllByNameLikeOrderByPriceAsc(userId, n, pageSize, offset);
                        });
                    } else {
                        return userService.fetchDefaultUserId().flatMapMany(userId -> {
                            return itemRepository.findAllByNameLikeOrderByIdDesc(userId, n, pageSize, offset);
                        });
                    }
                })
                .orElseGet(() -> {
                    if (SortType.ALPHA.equals(sortType)) {
                        return userService.fetchDefaultUserId().flatMapMany(userId -> {
                            return itemRepository.findAllOrderByTitleAsc(userId, pageSize, offset);
                        });
                    } else if (SortType.PRICE.equals(sortType)) {
                        return userService.fetchDefaultUserId().flatMapMany(userId -> {
                            return itemRepository.findAllOrderByPriceAsc(userId, pageSize, offset);
                        });
                    } else {
                        return userService.fetchDefaultUserId().flatMapMany(userId -> {
                            return itemRepository.findAllOrderByIdDesc(userId, pageSize, offset);
                        });
                    }
                })
                .map(item -> {
                    return new ItemDto(
                            item.getId(),
                            item.getTitle(),
                            String.valueOf(item.getPrice() / 100),
                            item.getDescription(),
                            String.valueOf(item.getImageId()),
                            item.getCount()
                    );
                });

        Mono<Long> totalCountMono = Optional.ofNullable(name)
                .map(itemRepository::countByNameLike)
                .orElse(itemRepository.count());

        return Mono.zip(itemsFlux.collectList(), totalCountMono).map(tuple -> {
            List<ItemDto> items = tuple.getT1();
            long totalCount = tuple.getT2();
            return new ItemsPage(items, totalCount, pageSize, pageNumber);
        });
    }

    @Override
    @Transactional
    public Mono<ItemDto> findById(Long id) {
        return userService.fetchDefaultUserId().flatMap(userId -> {
            return itemRepository.findItemById(userId, id).map(item -> {
                ItemDto itemDto = new ItemDto(
                        item.getId(),
                        item.getTitle(),
                        String.valueOf(item.getPrice() / 100),
                        item.getDescription(),
                        String.valueOf(item.getImageId()),
                        item.getCount()
                );
                return itemDto;
            }).switchIfEmpty(Mono.error(new RuntimeException()));
        });
    }

    @Override
    @Transactional
    public Mono<Long> createItem(CreateItemDto itemDto) {
        return cacheService.evictItemsCache().then(imagesService.upload(itemDto.getBase64Image())
                .map(imageId -> {
                    Item item = new Item();
                    item.setTitle(itemDto.getTitle());
                    item.setDescription(itemDto.getDescription());
                    item.setPrice(itemDto.getPriceRubles() * 100);
                    item.setImageId(imageId);
                    return item;
                })
                .flatMap(itemRepository::save)
                .map(Item::getId));
    }
}
