package com.happidreampets.app;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.happidreampets.app.controller.ProductControllerInterceptor;
import com.happidreampets.app.pre_filters.LoggingFilter;

@Configuration
public class FilterConfig implements WebMvcConfigurer  {

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

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(productControllerInterceptor())
                .addPathPatterns("/{animalId}/{categoryId}/**");
    }
}

