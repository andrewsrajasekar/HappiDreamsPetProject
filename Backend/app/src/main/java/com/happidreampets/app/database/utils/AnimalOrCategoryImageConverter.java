package com.happidreampets.app.database.utils;

import org.json.JSONObject;

import com.happidreampets.app.database.model.AnimalOrCategoryImage;

import jakarta.persistence.AttributeConverter;

public class AnimalOrCategoryImageConverter implements AttributeConverter<AnimalOrCategoryImage, String> {
    @Override
    public String convertToDatabaseColumn(AnimalOrCategoryImage image) {
        if (image == null) {
            return null;
        }
        return image.toJSON().toString();
    }

    @Override
    public AnimalOrCategoryImage convertToEntityAttribute(String columnValue) {
        if (columnValue == null) {
            return null;
        }
        return new AnimalOrCategoryImage().fromJSON(new JSONObject(columnValue));
    }
}
