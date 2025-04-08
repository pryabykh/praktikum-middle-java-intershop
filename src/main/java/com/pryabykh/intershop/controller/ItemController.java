package com.pryabykh.intershop.controller;

import com.pryabykh.intershop.dto.ItemsPage;
import com.pryabykh.intershop.enums.SortType;
import com.pryabykh.intershop.service.ItemService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/")
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public String root() {
        return "redirect:/main/items";
    }

    @GetMapping("/main/items")
    public String mainItems(@RequestParam(value = "search", required = false) String search,
                            @RequestParam(value = "sort", defaultValue = "NO") SortType sort,
                            @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                            @RequestParam(value = "pageNumber", defaultValue = "0") int pageNumber,
                            Model model) {
        ItemsPage itemsPage = itemService.findAll(search, sort, pageSize, pageNumber);
        model.addAttribute("paging", itemsPage.getPaging());
        model.addAttribute("items", itemsPage.getItems());
        model.addAttribute("sort", sort.name());
        model.addAttribute("search", search);
        return "main";
    }
}
