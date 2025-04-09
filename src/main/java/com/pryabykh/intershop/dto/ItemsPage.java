package com.pryabykh.intershop.dto;

import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

public class ItemsPage {

    private final Paging paging;
    private final List<List<ItemDto>> items;

    public ItemsPage(Page<ItemDto> page) {
        List<List<ItemDto>> items = new ArrayList<>();
        this.paging = new Paging(page);
        int rowCount = 1;
        for (ItemDto itemDto : page) {
            if (rowCount > 3) {
                rowCount = 1;
            }
            if (rowCount == 1) {
                List<ItemDto> row = new ArrayList<>();
                row.add(itemDto);
                items.add(row);
            } else {
                items.get(items.size() - 1).add(itemDto);
            }
            rowCount++;
        }
        this.items = items;
    }

    public Paging getPaging() {
        return paging;
    }

    public List<List<ItemDto>> getItems() {
        return items;
    }

    public static class Paging {

        private final Page<ItemDto> page;

        private final int pageSize;

        private final int pageNumber;

        public Paging(Page<ItemDto> page) {
            this.page = page;
            this.pageSize = page.getPageable().getPageSize();
            this.pageNumber = page.getPageable().getPageNumber();
        }

        public boolean hasPrevious() {
            return page.hasPrevious();
        }

        public boolean hasNext() {
            return page.hasNext();
        }

        public Page<ItemDto> getPage() {
            return page;
        }

        public int pageSize() {
            return pageSize;
        }

        public int pageNumber() {
            return pageNumber;
        }
    }
}
