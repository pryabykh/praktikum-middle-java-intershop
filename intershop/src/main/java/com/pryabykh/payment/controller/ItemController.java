package com.pryabykh.payment.controller;

import com.pryabykh.payment.dto.CreateItemDto;
import com.pryabykh.payment.enums.SortType;
import com.pryabykh.payment.service.ItemService;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.reactive.result.view.Rendering;
import reactor.core.publisher.Mono;

@Controller
@RequestMapping("/")
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public Mono<String> root() {
        return Mono.just("redirect:/main/items");
    }

    @GetMapping(value = "/main/items", produces = MediaType.TEXT_HTML_VALUE)
    public Mono<Rendering> mainItems(@RequestParam(value = "search", required = false) String search,
                                     @RequestParam(value = "sort", defaultValue = "NO") SortType sort,
                                     @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                     @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber) {
        return itemService.findAll(search, sort, pageSize, pageNumber)
                .map(itemsPage -> Rendering.view("main")
                        .modelAttribute("paging", itemsPage.getPaging())
                        .modelAttribute("items", itemsPage.getItems())
                        .modelAttribute("sort", sort.name())
                        .modelAttribute("search", search)
                        .build());
    }

    @GetMapping("/items/{id}")
    public Mono<Rendering> fetchItem(@PathVariable("id") Long id) {
        return itemService.findById(id)
                .map(item -> Rendering.view("item")
                        .modelAttribute("item", item)
                        .build());
    }

    @GetMapping("/create-item-form")
    public Mono<String> showCreateItemForm() {
        return Mono.just("create-item");
    }

    @PostMapping("/create-item")
    public Mono<String> createItem(@ModelAttribute CreateItemDto itemDto) {
        return itemService.createItem(itemDto)
                .then(Mono.just("redirect:/"));
    }
}
