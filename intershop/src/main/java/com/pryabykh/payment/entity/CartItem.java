package com.pryabykh.payment.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "intershop.carts")
public class CartItem {

    @Id
    private Long id;

    @Column("user_id")
    private Long userId;

    @Column("item_id")
    private Long itemId;

    @Column("count")
    private int count = 1;

    public CartItem() {
    }

    public CartItem(Long userId, int count) {
        this.userId = userId;
        this.count = count;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Long getItemId() {
        return itemId;
    }

    public void setItemId(Long itemId) {
        this.itemId = itemId;
    }
}
