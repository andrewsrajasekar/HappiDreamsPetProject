package com.happidreampets.app.database.model;

import org.json.JSONObject;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table
public class Cart {

    public enum CARTCOLUMN {
        ID("id"),
        USER_ID("user_id"),
        PRODUCT_ID("product_id"),
        ADDED_TIME("added_time"),
        QUANTITY("quantity");

        private final String columnName;

        CARTCOLUMN(String columnName) {
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

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(name = "added_time")
    private Long addedTime;

    @Column(name = "quantity")
    private Long quantity;

    public Cart() {
    }

    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getAddedTime() {
        return addedTime;
    }

    public void setAddedTime(Long added_time) {
        this.addedTime = added_time;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public JSONObject toJSON() {
        return new JSONObject(this);
    }
}
