package com.pryabykh.intershop.dto;

import java.util.List;

public class CartDto {

    private List<ItemDto> items;

    private long total;

    private boolean empty;

    private boolean possibleToBuy;

    private boolean available;

    public CartDto(List<ItemDto> items, long total, boolean empty) {
        this.items = items;
        this.total = total;
        this.empty = empty;
    }

    public CartDto(List<ItemDto> items, long total, boolean empty, boolean possibleToBuy, boolean available) {
        this.items = items;
        this.total = total;
        this.empty = empty;
        this.possibleToBuy = possibleToBuy;
        this.available = available;
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

    public boolean isPossibleToBuy() {
        return possibleToBuy;
    }

    public void setPossibleToBuy(boolean possibleToBuy) {
        this.possibleToBuy = possibleToBuy;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
