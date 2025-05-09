package com.pryabykh.payment.dto;

public class OrderItemDto {

    private Long id;

    private String title;

    private String price;

    private String imgPath;

    private int count;

    public OrderItemDto(Long id, String title, String price, String imgPath, int count) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.imgPath = imgPath;
        this.count = count;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getImgPath() {
        return imgPath;
    }

    public void setImgPath(String imgPath) {
        this.imgPath = imgPath;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
