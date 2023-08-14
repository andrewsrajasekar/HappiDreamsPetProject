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
public class UserAddress {
    public enum USERADDRESSCOLUMN {
        ID("id"),
        // USER_ID("user_id"),
        ADDRESS("address"),
        CITY("city"),
        STATE("state"),
        COUNTRY("country"),
        PINCODE("pincode"),
        IS_DEFAULT_ADDRESS("is_default_address"),
        TO_BE_DELETED("to_be_deleted"),
        TO_BE_DELETED_STATUSCHANGETIME("to_be_deleted_statusChangeTime"),
        ADDED_TIME("added_time"),
        ADDEDTIME("addedTime");

        private final String columnName;

        USERADDRESSCOLUMN(String columnName) {
            this.columnName = columnName;
        }

        public String getColumnName() {
            return columnName;
        }
    }

    public UserAddress(Long id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "address")
    private String address;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "country")
    private String country;

    @Column(name = "pincode")
    private String pincode;

    @Column(name = "is_default_address")
    private Boolean isDefaultAddress;

    @Column(name = "to_be_deleted")
    private Boolean toBeDeleted = Boolean.FALSE;

    @Column(name = "to_be_deleted_statusChangeTime")
    private Long toBeDeletedStatusChangeTime;

    @Column(name = "added_time")
    private Long addedTime;

    public UserAddress() {
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public Boolean getIsDefaultAddress() {
        return isDefaultAddress;
    }

    public void setIsDefaultAddress(Boolean isDefaultAddress) {
        this.isDefaultAddress = isDefaultAddress;
    }

    public Boolean getToBeDeleted() {
        return toBeDeleted;
    }

    public void setToBeDeleted(Boolean toBeDeleted) {
        this.toBeDeleted = toBeDeleted;
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

    public JSONObject toJSON() {
        return new JSONObject(this);
    }
}
