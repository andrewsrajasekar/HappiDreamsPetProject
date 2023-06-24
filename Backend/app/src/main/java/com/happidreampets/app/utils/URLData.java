package com.happidreampets.app.utils;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;
import org.springframework.core.type.classreading.SimpleMetadataReaderFactory;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.springframework.core.type.filter.TypeFilter;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.happidreampets.app.utils.AccessLevel.AccessEnum;

public class URLData {
    public static List<String> getUrlsWithoutAccessLevelAnnotation() throws ClassNotFoundException, IOException {
        List<String> urls = new ArrayList<>();
        List<Class<?>> allControllerClass = getAllControllerClasses();
        for (Class<?> controllerClass : allControllerClass) {
            // Get all methods in the controller class
            Method[] methods = controllerClass.getDeclaredMethods();

            // Iterate through each method
            for (Method method : methods) {
                // Check if the method has the annotation
                if (AnnotationUtils.findAnnotation(method, AccessLevel.class) == null) {
                    // Retrieve the URL mapping using RequestMapping annotation
                    RequestMapping requestMapping = AnnotationUtils.findAnnotation(method, RequestMapping.class);
                    if (requestMapping != null && requestMapping.value().length > 0) {
                        urls.add(requestMapping.value()[0]);
                    }
                }
            }
        }

        return urls;
    }

    public static List<String> getURLsWithAccessLevel(List<AccessEnum> accessEnum)
            throws ClassNotFoundException, IOException {
        List<String> urls = new ArrayList<>();
        List<Class<?>> allControllerClass = getAllControllerClasses();
        for (Class<?> controllerClass : allControllerClass) {
            Method[] methods = controllerClass.getDeclaredMethods();

            for (Method method : methods) {
                if (method.isAnnotationPresent(AccessLevel.class)) {
                    AccessLevel annotation = method.getAnnotation(AccessLevel.class);
                    if (accessEnum.contains(annotation.value())) {
                        RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                        if (requestMapping != null) {
                            String[] requestValues = requestMapping.value();
                            RequestMethod[] requestMethods = requestMapping.method();

                            for (String requestValue : requestValues) {
                                for (RequestMethod requestMethod : requestMethods) {
                                    urls.add(requestMethod.toString() + " " + requestValue);
                                }
                            }
                        }
                    }
                }
            }
        }

        return urls;
    }

    public static List<Class<?>> getAllControllerClasses() throws IOException, ClassNotFoundException {
        String basePackage = "com.happidreampets.app.controller";
        List<Class<?>> classes = new ArrayList<>();
        TypeFilter restControllerFilter = new AnnotationTypeFilter(RestController.class);
        MetadataReaderFactory metadataReaderFactory = new SimpleMetadataReaderFactory(
                new PathMatchingResourcePatternResolver());

        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        String packageSearchPath = "classpath*:" + basePackage.replace('.', '/') + "/**/*.class";
        org.springframework.core.io.Resource[] resources = resolver.getResources(packageSearchPath);

        for (org.springframework.core.io.Resource resource : resources) {
            MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
            String className = metadataReader.getClassMetadata().getClassName();
            if (restControllerFilter.match(metadataReader, metadataReaderFactory)) {
                Class<?> clazz = Class.forName(className);
                classes.add(clazz);
            }
        }

        return classes;
    }
}
