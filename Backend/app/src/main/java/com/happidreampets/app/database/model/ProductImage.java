package com.happidreampets.app.database.model;

import org.json.JSONObject;

public class ProductImage {
    private Long id;
    private String imageUrl;

    public ProductImage() {}

    public ProductImage(Long id, String imageUrl) {
        this.id = id;
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

    public JSONObject toJSON(){
        return new JSONObject( this );
    }

    public ProductImage fromJSON(JSONObject json) {
        Long id = json.getLong("id");
        String imageUrl = json.getString("imageUrl");
        return new ProductImage(id, imageUrl);
    } 
}