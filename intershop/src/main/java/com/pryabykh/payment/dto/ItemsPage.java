package com.pryabykh.payment.dto;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import java.util.ArrayList;
import java.util.List;

public class ItemsPage {

    private final Paging paging;
    private final List<List<ItemDto>> items;

    public ItemsPage(List<ItemDto> page, long totalCount, int pageSize, int pageNumber) {
        List<List<ItemDto>> items = new ArrayList<>();
        this.paging = new Paging(page, totalCount, pageSize, pageNumber);
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

        public Paging(List<ItemDto> page, long totalCount, int pageSize, int pageNumber) {
            Page<ItemDto> pageImpl = new PageImpl<>(page, PageRequest.of(pageNumber, pageSize), totalCount);
            this.page = pageImpl;
            this.pageSize = pageImpl.getPageable().getPageSize();
            this.pageNumber = pageImpl.getPageable().getPageNumber();
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
