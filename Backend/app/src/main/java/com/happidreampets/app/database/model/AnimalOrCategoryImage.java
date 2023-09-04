package com.happidreampets.app.database.model;

import org.json.JSONObject;

public class AnimalOrCategoryImage {
    private String imageType;

    private String imageUrl;

    public AnimalOrCategoryImage() {
    }

    public AnimalOrCategoryImage(String imageType, String imageUrl) {
        this.imageType = imageType;
        this.imageUrl = imageUrl;
    }

    public String getImageType() {
        return imageType;
    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public JSONObject toJSON() {
        return new JSONObject(this);
    }

    public AnimalOrCategoryImage fromJSON(JSONObject json) {
        String imageType = json.getString("imageType");
        String imageUrl = json.getString("imageUrl");
        return new AnimalOrCategoryImage(imageType, imageUrl);
    }
}
