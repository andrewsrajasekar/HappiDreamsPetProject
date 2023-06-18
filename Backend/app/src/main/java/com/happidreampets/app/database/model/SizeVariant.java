package com.happidreampets.app.database.model;

import org.json.JSONObject;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table
public class SizeVariant {

    public enum SIZEVARIANTCOLUMN {
        ID("id"),
        PRODUCT_ID("product_id"),
        VARIANTID("variantId"),
        ADDEDTIME("addedTime");

        private final String columnName;

        SIZEVARIANTCOLUMN(String columnName) {
            this.columnName = columnName;
        }

        public String getColumnName() {
            return columnName;
        }
    }
    
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    @JoinColumn(name = "product_id")
    private Product product;
    private Long variantId;

    public SizeVariant(){}
    
    public Long getId() {
        return id;
    }
    public Product getProduct() {
        return product;
    }
    public void setProduct(Product product) {
        this.product = product;
    }
    public Long getVariantId() {
        return variantId;
    }
    public void setVariantId(Long variantId) {
        this.variantId = variantId;
    }

    public JSONObject toJSON(){
        return new JSONObject( this );
    }
}
