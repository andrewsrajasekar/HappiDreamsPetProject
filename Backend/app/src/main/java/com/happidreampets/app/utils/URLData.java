package com.happidreampets.app.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.happidreampets.app.utils.AccessLevel.AccessEnum;

public class URLData {

    public static List<AccessEnum> getAccessRoleOfMethod(Method method) {
        List<AccessEnum> accessRoles = new ArrayList<>();
        if (method.isAnnotationPresent(AccessLevel.class)) {
            Annotation annotation = method.getAnnotation(AccessLevel.class);
            if (annotation instanceof AccessLevel) {
                AccessLevel accessLevel = (AccessLevel) annotation;
                AccessLevel.AccessEnum[] accessEnums = accessLevel.value();
                for (AccessLevel.AccessEnum accessEnum : accessEnums) {
                    accessRoles.add(accessEnum);
                }
            }
        }
        return accessRoles;
    }

}
