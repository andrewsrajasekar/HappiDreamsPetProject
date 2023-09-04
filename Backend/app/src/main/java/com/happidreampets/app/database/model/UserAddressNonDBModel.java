package com.happidreampets.app.database.model;

import org.json.JSONObject;

public class UserAddressNonDBModel {
    private Long id;

    private String address;

    private String city;

    private String state;

    private String country;

    private String pincode;

    private Boolean isDefaultAddress;

    public UserAddressNonDBModel() {
    }

    public UserAddressNonDBModel(UserAddress userAddressData) {
        this.id = userAddressData.getId();
        this.address = userAddressData.getAddress();
        this.city = userAddressData.getCity();
        this.state = userAddressData.getState();
        this.country = userAddressData.getCountry();
        this.pincode = userAddressData.getPincode();
        this.isDefaultAddress = userAddressData.getIsDefaultAddress();
    }

    public UserAddressNonDBModel(Long id, String address, String city, String state, String country, String pincode,
            Boolean isDefaultAddress) {
        this.id = id;
        this.address = address;
        this.city = city;
        this.state = state;
        this.country = country;
        this.pincode = pincode;
        this.isDefaultAddress = isDefaultAddress;
    }

    public Long getId() {
        return id;
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

    public JSONObject toJSON() {
        return new JSONObject(this);
    }
}
