package com.ecommerce.product.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductRequest {
    @NotBlank private String name;
    @NotBlank private String category;
    @NotNull @Positive private BigDecimal price;
    @NotNull @Positive private BigDecimal mrp;
    @NotBlank private String description;
    private String emoji;
    @Size(min = 2, max = 2) private List<String> bg = new ArrayList<>();
    private List<String> sizes = new ArrayList<>();
    @NotNull @DecimalMin("0.0") private Double rating = 0.0;
    @NotNull @PositiveOrZero private Integer reviews = 0;
    @NotNull @PositiveOrZero private Integer stockQuantity;

    public ProductRequest() {}
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public BigDecimal getMrp() { return mrp; }
    public void setMrp(BigDecimal mrp) { this.mrp = mrp; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getEmoji() { return emoji; }
    public void setEmoji(String emoji) { this.emoji = emoji; }
    public List<String> getBg() { return bg; }
    public void setBg(List<String> bg) { this.bg = bg; }
    public List<String> getSizes() { return sizes; }
    public void setSizes(List<String> sizes) { this.sizes = sizes; }
    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }
    public Integer getReviews() { return reviews; }
    public void setReviews(Integer reviews) { this.reviews = reviews; }
    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }
}
