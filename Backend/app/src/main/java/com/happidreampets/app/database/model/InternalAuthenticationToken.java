package com.happidreampets.app.database.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Index;

@Entity
@Table(indexes = { @Index(columnList = ("user_id"), name = "idx_internal_authentication", unique = true) })
public class InternalAuthenticationToken {

    public enum INTERNALAUTHENTICATIONTOKEN {
        ID("id"),
        TOKEN("token"),
        USER_ID("user_id"),
        EXPIRING_TIME("expiring_time"),
        ADDED_TIME("added_time");

        private final String columnName;

        INTERNALAUTHENTICATIONTOKEN(String columnName) {
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

    @Column(name = "token")
    private String token;

    @OneToOne
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    @Column(name = "expiring_time")
    private Long expiringTime;

    @Column(name = "created_time")
    private Long createdTime;

    @Column(name = "added_time")
    private Long addedTime;

    public InternalAuthenticationToken() {
    }

    public Long getId() {
        return id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Long getExpiringTime() {
        return expiringTime;
    }

    public void setExpiringTime(Long expiringTime) {
        this.expiringTime = expiringTime;
    }

    public Long getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Long createdTime) {
        this.createdTime = createdTime;
    }

    public Long getAddedTime() {
        return addedTime;
    }

    public void setAddedTime(Long addedTime) {
        this.addedTime = addedTime;
    }
}
