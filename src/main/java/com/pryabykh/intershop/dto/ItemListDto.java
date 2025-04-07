package com.pryabykh.intershop.dto;

public class ItemListDto {

    private Long id;

    private String title;

    private String price;

    private String description;

    private String imgPath;

    private int count;

    public ItemListDto(Long id, String title, String price, String description, String imgPath, int count) {
        this.id = id;
        this.title = title;
        this.price = price;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
