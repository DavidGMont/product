package me.davidgarmo.soundseeker.product.persistence.entity;

import java.util.Set;

public class Product {
    private Long id;
    private String name;
    private String description;
    private String brand;
    private Double price;
    private Boolean available;
    private Set<String> images;
    private Long categoryId;

    public Product(String name, String description, String brand, Double price, Boolean available, Set<String> images, Long categoryId) {
        this.name = name;
        this.description = description;
        this.brand = brand;
        this.price = price;
        this.available = available;
        this.images = images;
        this.categoryId = categoryId;
    }

    public Product(Long id, String name, String description, String brand, Double price, Boolean available, Set<String> images, Long categoryId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.brand = brand;
        this.price = price;
        this.available = available;
        this.images = images;
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

    public Set<String> getImages() {
        return images;
    }

    public void setImages(Set<String> images) {
        this.images = images;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", brand='" + brand + '\'' +
                ", price=" + price +
                ", available=" + available +
                ", images=" + images +
                ", categoryId=" + categoryId +
                '}';
    }
}
