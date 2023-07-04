package com.happidreampets.app.database.utils;

import org.json.JSONObject;

import com.happidreampets.app.database.model.ProductImage;

import jakarta.persistence.AttributeConverter;

public class ProductImageConverter implements AttributeConverter<ProductImage, String> {
     @Override
    public String convertToDatabaseColumn(ProductImage productImage) {
        if (productImage == null) {
            return null;
        }
        return productImage.toJSON().toString();
    }

    @Override
    public ProductImage convertToEntityAttribute(String columnValue) {
        if (columnValue == null) {
            return null;
        }
        return new ProductImage().fromJSON(new JSONObject(columnValue));
    }
}
