package com.happidreampets.app.database.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.happidreampets.app.constants.ProductConstants;
import com.happidreampets.app.database.utils.AnimalOrCategoryImageConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table
public class Animal {

    public enum ANIMALCOLUMN {
        ID("id"),
        NAME("name"),
        DESCRIPTION("description"),
        IMAGE(ProductConstants.LowerCase.IMAGE),
        TO_BE_DELETED("to_be_deleted"),
        TO_BE_DELETED_STATUSCHANGETIME("to_be_deleted_statusChangeTime"),
        ADDED_TIME("added_time");

        private final String columnName;

        ANIMALCOLUMN(String columnName) {
            this.columnName = columnName;
        }

        public String getColumnName() {
            return columnName;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = ProductConstants.LowerCase.IMAGE)
    @Convert(converter = AnimalOrCategoryImageConverter.class)
    private AnimalOrCategoryImage image;

    @Column(name = "to_be_deleted")
    private Boolean toBeDeleted = Boolean.FALSE;

    @Column(name = "to_be_deleted_statusChangeTime")
    private Long toBeDeletedStatusChangeTime;

    @Column(name = "added_time")
    private Long addedTime;

    public Animal() {
    }

    public Long getId() {
        return id;
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

    public AnimalOrCategoryImage getImage() {
        return image;
    }

    public void setImage(AnimalOrCategoryImage image) {
        this.image = image;
    }

    public Boolean getToBeDeleted() {
        return toBeDeleted;
    }

    public void setToBeDeleted(Boolean to_be_deleted) {
        this.toBeDeleted = to_be_deleted;
    }

    public Long getToBeDeletedStatusChangeTime() {
        return toBeDeletedStatusChangeTime;
    }

    public void setToBeDeletedStatusChangeTime(Long toBeDeletedStatusChangeTime) {
        this.toBeDeletedStatusChangeTime = toBeDeletedStatusChangeTime;
    }

    public Long getAddedTime() {
        return addedTime;
    }

    public void setAddedTime(Long addedTime) {
        this.addedTime = addedTime;
    }

    public JSONObject toJSON(Boolean isSystemDataExcluded, Boolean isComplexImageFieldExcluded) {
        JSONObject data = new JSONObject(this);

        if (isSystemDataExcluded) {
            for (String field : getSystemFields()) {
                if (data.has(field)) {
                    data.remove(field);
                }
            }
        }
        // if (isComplexImageFieldExcluded &&
        // data.has(ProductConstants.LowerCase.IMAGE)) {
        // AnimalOrCategoryImage image = data.get(ProductConstants.LowerCase.IMAGE) !=
        // null
        // ? this.image
        // : null;
        // data.remove(ProductConstants.LowerCase.IMAGE);
        // if (image != null) {
        // data.put(ProductConstants.LowerCase.IMAGE, image.getImageUrl());
        // } else {
        // data.put(ProductConstants.LowerCase.IMAGE, JSONObject.NULL);
        // }
        // }
        return data;
    }

    private List<String> getSystemFields() {
        List<String> systemFields = new ArrayList<>();
        systemFields.add("toBeDeleted");
        systemFields.add("toBeDeletedStatusChangeTime");
        systemFields.add("addedTime");
        return systemFields;
    }
}
