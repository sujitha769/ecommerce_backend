package com.ecommerce.product.dto;

import java.math.BigDecimal;
import java.util.List;

public class ProductResponse {
    private Long id;
    private String name;
    private String category;
    private BigDecimal price;
    private BigDecimal mrp;
    private Double rating;
    private Integer reviews;
    private String emoji;
    private List<String> bg;
    private String description;
    private List<String> sizes;
    private String imageUrl;

    public ProductResponse() {}
    public ProductResponse(Long id, String name, String category, BigDecimal price,
                           BigDecimal mrp, Double rating, Integer reviews, String emoji,
                           List<String> bg, String description, List<String> sizes, String imageUrl) {
        this.id = id; this.name = name; this.category = category; this.price = price;
        this.mrp = mrp; this.rating = rating; this.reviews = reviews; this.emoji = emoji;
        this.bg = bg; this.description = description; this.sizes = sizes; this.imageUrl = imageUrl;
    }
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public BigDecimal getPrice() { return price; }
    public BigDecimal getMrp() { return mrp; }
    public Double getRating() { return rating; }
    public Integer getReviews() { return reviews; }
    public String getEmoji() { return emoji; }
    public List<String> getBg() { return bg; }
    public String getDescription() { return description; }
    public List<String> getSizes() { return sizes; }
    public String getImageUrl() { return imageUrl; }
}
