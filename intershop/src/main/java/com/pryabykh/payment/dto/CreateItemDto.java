package com.pryabykh.payment.dto;

public class CreateItemDto {

    private String title;

    private String description;

    private Long priceRubles;

    private String base64Image;

    public CreateItemDto() {
    }

    public CreateItemDto(String title, String description, Long priceRubles, String base64Image) {
        this.title = title;
        this.description = description;
        this.priceRubles = priceRubles;
        this.base64Image = base64Image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Long getPriceRubles() {
        return priceRubles;
    }

    public void setPriceRubles(Long priceRubles) {
        this.priceRubles = priceRubles;
    }

    public String getBase64Image() {
        return base64Image;
    }

    public void setBase64Image(String base64Image) {
        this.base64Image = base64Image;
    }
}
