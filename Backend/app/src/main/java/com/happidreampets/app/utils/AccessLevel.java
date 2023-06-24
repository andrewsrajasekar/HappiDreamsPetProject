package com.happidreampets.app.utils;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AccessLevel {
    public enum AccessEnum {
        USER,
        ADMIN
    }

    AccessEnum value();
}
