package com.happidreampets.app.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.happidreampets.app.database.model.User.USER_ROLE;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AccessLevel {
    public enum AccessEnum {
        USER,
        ADMIN;
    }

    public class Convertion {
        public static AccessEnum toAccessEnumFromRole(USER_ROLE role) {
            if (role.equals(USER_ROLE.ADMIN)) {
                return AccessEnum.ADMIN;
            } else if (role.equals(USER_ROLE.USER)) {
                return AccessEnum.USER;
            } else {
                return null;
            }
        }
    }

    AccessEnum[] value();
}
