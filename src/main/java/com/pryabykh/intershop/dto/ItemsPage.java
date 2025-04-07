package com.pryabykh.intershop.dto;

import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

public class ItemsPage {

    private final Paging paging;
    private final List<List<ItemListDto>> items;

    public ItemsPage(Page<ItemListDto> page) {
        List<List<ItemListDto>> items = new ArrayList<>();
        this.paging = new Paging(page);
        int rowCount = 1;
        for (ItemListDto itemListDto : page) {
            if (rowCount > 3) {
                rowCount = 1;
            }
            if (rowCount == 1) {
                List<ItemListDto> row = new ArrayList<>();
                row.add(itemListDto);
                items.add(row);
            } else {
                items.get(items.size() - 1).add(itemListDto);
            }
            rowCount++;
        }
        this.items = items;
    }

    public Paging getPaging() {
        return paging;
    }

    public List<List<ItemListDto>> getItems() {
        return items;
    }

    public static class Paging {

        private final Page<ItemListDto> page;

        private final int pageSize;

        private final int pageNumber;

        public Paging(Page<ItemListDto> page) {
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

        public Page<ItemListDto> getPage() {
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
