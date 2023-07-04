package com.happidreampets.app.database.model;

import java.util.List;

import org.json.JSONObject;

import com.happidreampets.app.database.utils.ProductImageConverter;
import com.happidreampets.app.database.utils.WeightUnitConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table
public class Product {

    public enum WEIGHT_UNITS {
        NONE(-1),
        GRAM(0),
        KILOGRAM(1),
        MILLILITER(2),
        LITER(3);

        Integer unit;

        WEIGHT_UNITS(Integer unit) {
            this.unit = unit;
        }

        public Integer getUnit() {
            return this.unit;
        }

    }

    public enum PRODUCTCOLUMN {
        ID("id"),
        NAME("name"),
        DESCRIPTION("description"),
        DETAILS("details"),
        COLOR("color"),
        SIZE("size"),
        WEIGHT_UNIT("weight_unit"),
        WEIGHT("weight"),
        STOCKS("stocks"),
        PRICE("price"),
        IS_VISIBLE("is_visible"),
        VARIANT_SIZE_ID("variant_size_id"),
        VARIANT_COLOR_ID("variant_color_id"),
        VARIANT_WEIGHT_ID("variant_weight_id"),
        CATEGORY_ID("category_id"),
        TO_BE_DELETED("to_be_deleted"),
        TO_BE_DELETED_STATUSCHANGETIME("to_be_deleted_statusChangeTime"),
        THUMBNAIL_IMAGE_URL("thumbnail_image_url"),
        IMAGE_URLS("image_urls"),
        ADDED_TIME("added_time");

        private final String columnName;

        PRODUCTCOLUMN(String columnName) {
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

    @Column(name = "details")
    private String details;

    @Column(name = "color")
    private String color;

    @Column(name = "size")
    private String size;

    @Column(name = "weight_unit")
    @Convert(converter = WeightUnitConverter.class)
    private WEIGHT_UNITS weightUnits;

    @Column(name = "weight")
    private Integer weight;

    @Column(name = "stocks")
    private Long stocks;

    @Column(name = "price")
    private Long price;

    @Column(name = "is_visible")
    private Boolean isVisible;

    @Column(name = "variant_size_id")
    private Long variantSizeId;

    @Column(name = "variant_color_id")
    private Long variantColorId;

    @Column(name = "variant_weight_id")
    private Long variantWeightId;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "to_be_deleted")
    private Boolean toBeDeleted = Boolean.FALSE;

    @Column(name = "to_be_deleted_statusChangeTime")
    private Long toBeDeletedStatusChangeTime;

    @Column(name = "thumbnail_image_url")
    @Convert(converter = ProductImageConverter.class)
    private ProductImage thumbnailImageUrl;

    @Column(name = "image_urls")
    @Convert(converter = ProductImageConverter.class)
    private List<ProductImage> imageUrls;

    @Column(name = "added_time")
    private Long addedTime;

    public Product() {
    }

    public Product(Long id, String name, String color, String size, WEIGHT_UNITS weightUnits, Integer weight,
            Long stocks, Long price, Category category, ProductImage thumbnailImageUrl) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.size = size;
        this.weightUnits = weightUnits;
        this.weight = weight;
        this.stocks = stocks;
        this.price = price;
        this.category = category;
        this.thumbnailImageUrl = thumbnailImageUrl;
    }

    public Product(Long id, String name, String color, String size, WEIGHT_UNITS weightUnits, Integer weight,
            Long stocks, Long price, Category category, ProductImage thumbnailImageUrl, List<ProductImage> imageUrls) {
        this.id = id;
        this.name = name;
        this.color = color;
        this.size = size;
        this.weightUnits = weightUnits;
        this.weight = weight;
        this.stocks = stocks;
        this.price = price;
        this.category = category;
        this.thumbnailImageUrl = thumbnailImageUrl;
        this.imageUrls = imageUrls;
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

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public WEIGHT_UNITS getWeightUnits() {
        return weightUnits;
    }

    public void setWeightUnits(WEIGHT_UNITS weight_units) {
        this.weightUnits = weight_units;
    }

    public Integer getWeight() {
        return weight;
    }

    public void setWeight(Integer weight) {
        this.weight = weight;
    }

    public Long getStocks() {
        return stocks;
    }

    public void setStocks(Long stocks) {
        this.stocks = stocks;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Boolean getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(Boolean isVisible) {
        this.isVisible = isVisible;
    }

    public Long getVariantSizeId() {
        return variantSizeId;
    }

    public void setVariantSizeId(Long variant_size_id) {
        this.variantSizeId = variant_size_id;
    }

    public Long getVariantColorId() {
        return variantColorId;
    }

    public void setVariantColorId(Long variant_color_id) {
        this.variantColorId = variant_color_id;
    }

    public Long getVariantWeightId() {
        return variantWeightId;
    }

    public void setVariantWeightId(Long variant_weight_id) {
        this.variantWeightId = variant_weight_id;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
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

    public ProductImage getThumbnailImageUrl() {
        return thumbnailImageUrl;
    }

    public void setThumbnailImageUrl(ProductImage thumbnailImageUrl) {
        this.thumbnailImageUrl = thumbnailImageUrl;
    }

    public List<ProductImage> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<ProductImage> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public Long getAddedTime() {
        return addedTime;
    }

    public void setAddedTime(Long added_time) {
        this.addedTime = added_time;
    }

    public JSONObject toJSON() {
        return new JSONObject(this);
    }
}
