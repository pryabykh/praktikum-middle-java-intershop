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

    public ItemServiceImpl(ItemRepository itemRepository,
                           ImagesService imagesService) {
        this.itemRepository = itemRepository;
        this.imagesService = imagesService;
    }

    @Override
    @Transactional(readOnly = true)
    public Mono<ItemsPage> findAll(String name, SortType sortType, int pageSize, int pageNumber) {
        int offset = pageNumber * pageSize;

        Flux<ItemDto> itemsFlux = Optional.ofNullable(name)
                .map(n -> {
                    if (SortType.ALPHA.equals(sortType)) {
                        return itemRepository.findAllByNameLikeOrderByTitleAsc(n, pageSize, offset);
                    } else if (SortType.PRICE.equals(sortType)) {
                        return itemRepository.findAllByNameLikeOrderByPriceAsc(n, pageSize, offset);
                    } else {
                        return itemRepository.findAllByNameLikeOrderByIdDesc(n, pageSize, offset);
                    }
                })
                .orElseGet(() -> {
                    if (SortType.ALPHA.equals(sortType)) {
                        return itemRepository.findAllOrderByTitleAsc(pageSize, offset);
                    } else if (SortType.PRICE.equals(sortType)) {
                        return itemRepository.findAllOrderByPriceAsc(pageSize, offset);
                    } else {
                        return itemRepository.findAllOrderByIdDesc(pageSize, offset);
                    }
                })
                .map(item -> {
                    return new ItemDto(
                            item.getId(),
                            item.getTitle(),
                            String.valueOf(item.getPrice() / 100),
                            item.getDescription(),
                            String.valueOf(item.getImageId())
                    );
                });

//        Flux<ItemDto> itemsWithCount = assignCountToItems(itemsFlux); //todo fixme

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
        return itemRepository.findById(id).map(item -> {
            ItemDto itemDto = new ItemDto(
                    item.getId(),
                    item.getTitle(),
                    String.valueOf(item.getPrice() / 100),
                    item.getDescription(),
                    String.valueOf(item.getImageId())
            );
//            assignCountToItems(List.of(itemDto)); //todo fixme
            return itemDto;
        }).switchIfEmpty(Mono.error(new RuntimeException()));
    }

    @Override
    @Transactional
    public Mono<Long> createItem(CreateItemDto itemDto) {
        return imagesService.upload(itemDto.getBase64Image())
                .map(imageId -> {
                    Item item = new Item();
                    item.setTitle(itemDto.getTitle());
                    item.setDescription(itemDto.getDescription());
                    item.setPrice(itemDto.getPriceRubles() * 100);
                    item.setImageId(imageId);
                    return item;
                })
                .flatMap(itemRepository::save)
                .map(Item::getId);
    }
}
