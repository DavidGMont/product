package me.davidgarmo.soundseeker.product.persistence.entity;

import java.util.Locale;

public class Product {
    private Long id;
    private String name;
    private String description;
    private String brand;
    private Double price;
    private Boolean available;
    private String thumbnail;
    private Long categoryId;

    public Product(String name, String description, String brand, Double price, Boolean available, String thumbnail, Long categoryId) {
        this.name = name;
        this.description = description;
        this.brand = brand;
        this.price = price;
        this.available = available;
        this.thumbnail = thumbnail;
        this.categoryId = categoryId;
    }

    public Product(Long id, String name, String description, String brand, Double price, Boolean available, String thumbnail, Long categoryId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.brand = brand;
        this.price = price;
        this.available = available;
        this.thumbnail = thumbnail;
        this.categoryId = categoryId;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Boolean getAvailable() {
        return available;
    }

    public void setAvailable(Boolean available) {
        this.available = available;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public String toString() {
        return String.format(Locale.ENGLISH,
                """
                        {
                            id: %s,
                            name: "%s",
                            description: "%s",
                            brand: "%s",
                            price: %.2f,
                            available: %b,
                            thumbnail: "%s",
                            categoryId: %s
                        }
                        """,
                id, name, description, brand, price, available, thumbnail, categoryId);
    }
}
