package com.pryabykh.payment.dto;

import java.util.List;

public class OrderDto {

    private Long id;

    private Long totalSum;

    private List<OrderItemDto> items;

    public OrderDto(Long id, Long totalSum, List<OrderItemDto> items) {
        this.id = id;
        this.totalSum = totalSum;
        this.items = items;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getTotalSum() {
        return totalSum;
    }

    public void setTotalSum(Long totalSum) {
        this.totalSum = totalSum;
    }

    public List<OrderItemDto> getItems() {
        return items;
    }

    public void setItems(List<OrderItemDto> items) {
        this.items = items;
    }
}
