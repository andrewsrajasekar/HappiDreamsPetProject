package com.happidreampets.app.database.model;

import org.json.JSONObject;

public class ProductImage {
    private Long id;
    private String imageUrl;
    private String imageType;

    public ProductImage() {
    }

    public ProductImage(Long id, String imageType, String imageUrl) {
        this.id = id;
        this.imageType = imageType;
        this.imageUrl = imageUrl;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public JSONObject toJSON() {
        return new JSONObject(this);
    }

    public ProductImage fromJSON(JSONObject json) {
        Long id = json.getLong("id");
        String imageType = json.getString("imageType");
        String imageUrl = json.getString("imageUrl");
        return new ProductImage(id, imageType, imageUrl);
    }
}