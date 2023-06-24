package com.happidreampets.app;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.happidreampets.app.controller.OtherDataControllerInterceptor;
import com.happidreampets.app.controller.ProductControllerInterceptor;
import com.happidreampets.app.controller.UserControllerInterceptor;
import com.happidreampets.app.pre_filters.LoggingFilter;

@Configuration
public class FilterConfig implements WebMvcConfigurer {

    @Bean
    public FilterRegistrationBean<LoggingFilter> loggingFilter() {
        FilterRegistrationBean<LoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new LoggingFilter());
        registrationBean.addUrlPatterns("/*"); // Set the URL patterns you want to apply the filter to
        return registrationBean;
    }

    @Bean
    ProductControllerInterceptor productControllerInterceptor() {
        return new ProductControllerInterceptor();
    }

    @Bean
    OtherDataControllerInterceptor otherDataControllerInterceptor() {
        return new OtherDataControllerInterceptor();
    }

    @Bean
    UserControllerInterceptor userControllerInterceptor() {
        return new UserControllerInterceptor();
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        addProductInterceptors(registry);
        addOtherDataInterceptors(registry);
    }

    public void addProductInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(productControllerInterceptor())
                .addPathPatterns("/animal/{animalId}/category/{categoryId}/**");
    }

    public void addOtherDataInterceptors(InterceptorRegistry registry) {
        List<String> pathPatterns = new ArrayList<>();
        pathPatterns.add("/top-category/{categoryId}");
        pathPatterns.add("/top-category/{categoryId}/product/{productId}");
        pathPatterns.add("/animal/{animalId}");
        pathPatterns.add("/animal/{animalId}/image");
        pathPatterns.add("/animal/{animalId}/categories");
        pathPatterns.add("/animal/{animalId}/category/{categoryId}");
        pathPatterns.add("/animal/{animalId}/category");
        pathPatterns.add("/animal/{animalId}/category/{categoryId}/image");
        registry.addInterceptor(otherDataControllerInterceptor())
                .addPathPatterns(pathPatterns);
    }

    public void addUserInterceptors(InterceptorRegistry registry) {
        List<String> pathPatterns = new ArrayList<>();
        pathPatterns.add("/user/**");
        registry.addInterceptor(userControllerInterceptor())
                .addPathPatterns(pathPatterns);
    }
}
