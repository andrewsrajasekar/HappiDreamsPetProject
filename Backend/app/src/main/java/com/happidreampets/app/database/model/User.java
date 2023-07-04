package com.happidreampets.app.database.model;

import org.json.JSONObject;

import com.happidreampets.app.database.utils.UserRoleConverter;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table
public class User {

    public enum USER_ROLE {
        ADMIN(1),
        USER(2);

        Integer roleId;

        USER_ROLE(Integer roleId) {
            this.roleId = roleId;
        }

        public Integer getRoleId() {
            return this.roleId;
        }

    }

    public enum USERCOLUMN {
        ID("id"),
        NAME("name"),
        PASSWORD("password"),
        EMAIL("email"),
        PHONE_EXTENSION("phone_extension"),
        PHONE_NUMBER("phone_number"),
        DEFAULT_ADDRESS_ID("default_address_id");

        private final String columnName;

        USERCOLUMN(String columnName) {
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

    @Column(name = "password")
    private String password;

    @Column(name = "email", unique = true)
    private String email;

    @Column(name = "phone_extension")
    private String phone_extension;

    @Column(name = "phone_number")
    private String phone_number;

    @OneToOne
    @JoinColumn(name = "default_address_id")
    private UserAddress defaultAddress;

    @Column(name = "role")
    @Convert(converter = UserRoleConverter.class)
    private USER_ROLE role;

    @Column(name = "added_time")
    private Long addedTime;

    public User() {
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone_extension() {
        return phone_extension;
    }

    public void setPhone_extension(String phone_extension) {
        this.phone_extension = phone_extension;
    }

    public String getPhone_number() {
        return phone_number;
    }

    public void setPhone_number(String phone_number) {
        this.phone_number = phone_number;
    }

    public UserAddress getDefaultAddress() {
        return defaultAddress;
    }

    public void setDefaultAddress(UserAddress defaultAddress) {
        this.defaultAddress = defaultAddress;
    }

    public USER_ROLE getRole() {
        return role;
    }

    public void setRole(USER_ROLE role) {
        this.role = role;
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
