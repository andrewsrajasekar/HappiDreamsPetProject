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
public class OrderHistory {

    public enum ORDERHISTORYCOLUMN {
        ID("id"),
        USER_ID("user_id"),
        PRODUCT_ID("product_id"),
        ADDED_TIME("added_time");

        private final String columnName;

        ORDERHISTORYCOLUMN(String columnName) {
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

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_ids")
    private Product[] products;

    @ManyToOne
    @JoinColumn(name = "address_id")
    private UserAddress address;

    @Column(name = "added_time")
    private Long addedTime;

    public OrderHistory() {
    }

    public Long getId() {
        return id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Product[] getProducts() {
        return products;
    }

    public void setProducts(Product[] products) {
        this.products = products;
    }

    public Long getAddedTime() {
        return addedTime;
    }

    public void setAddedTime(Long addedTime) {
        this.addedTime = addedTime;
    }

    public UserAddress getAddress() {
        return address;
    }

    public void setAddress(UserAddress address) {
        this.address = address;
    }

    public JSONObject toJSON() {
        return new JSONObject(this);
    }
}
