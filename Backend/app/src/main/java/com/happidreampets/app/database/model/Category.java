package com.happidreampets.app.database.model;

import org.json.JSONObject;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table
public class Category {

    public enum CATEGORYCOLUMN {
        ID("id"),
        NAME("name"),
        DESCRIPTION("description"),
        IMAGE("image"),
        TO_BE_DELETED("to_be_deleted"),
        TO_BE_DELETED_STATUSCHANGETIME("to_be_deleted_statusChangeTime"),
        ADDED_TIME("added_time"),
        ANIMAL_ID("animal_id");

        private final String columnName;

        CATEGORYCOLUMN(String columnName) {
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

    @Column(name = "image")
    private String image;

    @Column(name = "to_be_deleted")
    private Boolean toBeDeleted = Boolean.FALSE;

    @Column(name = "to_be_deleted_statusChangeTime")
    private Long toBeDeletedStatusChangeTime;

    @Column(name = "added_time")
    private Long addedTime;

    @ManyToOne
    @JoinColumn(name = "animal_id")
    private Animal animal;

    public Category() {
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

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
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

    public Animal getAnimal() {
        return animal;
    }

    public void setAnimal(Animal animal) {
        this.animal = animal;
    }

    public JSONObject toJSON() {
        return new JSONObject(this);
    }
}
