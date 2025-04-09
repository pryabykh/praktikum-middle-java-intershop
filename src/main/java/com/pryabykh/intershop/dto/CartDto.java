package com.pryabykh.intershop.dto;

import java.util.List;

public class CartDto {

    private List<ItemDto> items;

    private long total;

    private boolean empty;

    public CartDto(List<ItemDto> items, long total, boolean empty) {
        this.items = items;
        this.total = total;
        this.empty = empty;
    }

    public List<ItemDto> getItems() {
        return items;
    }

    public void setItems(List<ItemDto> items) {
        this.items = items;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public boolean isEmpty() {
        return empty;
    }

    public void setEmpty(boolean empty) {
        this.empty = empty;
    }
}
