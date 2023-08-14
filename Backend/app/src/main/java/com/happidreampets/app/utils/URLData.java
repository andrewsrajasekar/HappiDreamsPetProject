package com.happidreampets.app.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.bind.annotation.RequestMapping;

import com.happidreampets.app.controller.OtherDataController;
import com.happidreampets.app.controller.ProductController;
import com.happidreampets.app.controller.UserController;
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

    public static List<String> getAllOtherDataControllerURL() {
        Class<?> controllerClass = OtherDataController.class;
        RequestMapping parentRequestMapping = controllerClass.getAnnotation(RequestMapping.class);
        String parentUrl = "";
        if (parentRequestMapping != null) {
            String[] urlPatterns = parentRequestMapping.value();
            parentUrl = urlPatterns[0];
        }
        List<String> urlData = new ArrayList<>();
        Method[] methods = controllerClass.getDeclaredMethods();
        for (Method method : methods) {
            RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
            if (requestMapping != null) {
                String[] urlPatterns = requestMapping.value();
                for (String urlPattern : urlPatterns) {
                    urlData.add(parentUrl + urlPattern);
                }
            }
        }
        return urlData;
    }

    public static List<String> getAllUserControllerURL() {
        Class<?> controllerClass = UserController.class;
        RequestMapping parentRequestMapping = controllerClass.getAnnotation(RequestMapping.class);
        String parentUrl = "";
        if (parentRequestMapping != null) {
            String[] urlPatterns = parentRequestMapping.value();
            parentUrl = urlPatterns[0];
        }
        List<String> urlData = new ArrayList<>();
        Method[] methods = controllerClass.getDeclaredMethods();
        for (Method method : methods) {
            RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
            if (requestMapping != null) {
                String[] urlPatterns = requestMapping.value();
                for (String urlPattern : urlPatterns) {
                    urlData.add(parentUrl + urlPattern);
                }
            }
        }
        return urlData;
    }

    public static List<String> getAllProductControllerURL() {
        Class<?> controllerClass = ProductController.class;
        RequestMapping parentRequestMapping = controllerClass.getAnnotation(RequestMapping.class);
        String parentUrl = "";
        if (parentRequestMapping != null) {
            String[] urlPatterns = parentRequestMapping.value();
            parentUrl = urlPatterns[0];
        }
        List<String> urlData = new ArrayList<>();
        Method[] methods = controllerClass.getDeclaredMethods();
        for (Method method : methods) {
            RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
            if (requestMapping != null) {
                String[] urlPatterns = requestMapping.value();
                for (String urlPattern : urlPatterns) {
                    urlData.add(parentUrl + urlPattern);
                }
            }
        }
        return urlData;
    }

}
