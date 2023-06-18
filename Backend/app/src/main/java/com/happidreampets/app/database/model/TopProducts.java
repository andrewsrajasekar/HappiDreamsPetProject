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
public class TopProducts {

    public enum TOPPRODUCTSCOLUMN {
        ID("id"),
        PRODUCT_ID("product_id"),
        ORDER_NUMBER("order_number");

        private final String columnName;

        TOPPRODUCTSCOLUMN(String columnName) {
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
    private Integer order_number;

    public TopProducts(){}

    public Long getId() {
        return id;
    }
    public Product getProduct() {
        return product;
    }
    public void setProduct(Product product) {
        this.product = product;
    }
    public Integer getOrder_number() {
        return order_number;
    }
    public void setOrder_number(Integer order_number) {
        this.order_number = order_number;
    }

    public JSONObject toJSON(){
        return new JSONObject( this );
    }
}
