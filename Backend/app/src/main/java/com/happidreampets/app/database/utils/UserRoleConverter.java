package com.happidreampets.app.database.utils;

import com.happidreampets.app.database.model.User.USER_ROLE;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class UserRoleConverter implements AttributeConverter<USER_ROLE, Integer> {

    @Override
    public Integer convertToDatabaseColumn(USER_ROLE userRole) {
        if (userRole == null) {
            return null;
        }
        return userRole.getRoleId();
    }

    @Override
    public USER_ROLE convertToEntityAttribute(Integer roleId) {
        if (roleId == null) {
            return null;
        }
        for (USER_ROLE userRole : USER_ROLE.values()) {
            if (userRole.getRoleId().equals(roleId)) {
                return userRole;
            }
        }
        throw new IllegalArgumentException("Unknown roleId: " + roleId);
    }
}
