package com.happidreampets.app;

import java.util.List;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.happidreampets.app.controller.OtherDataControllerInterceptor;
import com.happidreampets.app.controller.ProductControllerInterceptor;
import com.happidreampets.app.controller.UserControllerInterceptor;
import com.happidreampets.app.pre_filters.LoggingFilter;
import com.happidreampets.app.utils.URLData;

@Configuration
// @EnableWebMvc
@ComponentScan("com.happidreampets.app.pre_filters")
public class FilterConfig implements WebMvcConfigurer {

    // @Override
    // public void addResourceHandlers(ResourceHandlerRegistry registry) {
    // registry.addResourceHandler("/static/**").addResourceLocations("classpath:/static/");
    // }

    @Bean
    public FilterRegistrationBean<LoggingFilter> loggingFilter() {
        FilterRegistrationBean<LoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new LoggingFilter());
        registrationBean.addUrlPatterns("/*"); // Set the URL patterns you want to apply the filter to
        return registrationBean;
    }

    @Bean
    public HandlerMapping handlerMapping() {
        return new RequestMappingHandlerMapping();
    }

    @Bean
    public DispatcherServlet dispatcherServlet() {
        return new DispatcherServlet();
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
        addUserInterceptors(registry);
    }

    public void addProductInterceptors(InterceptorRegistry registry) {
        List<String> pathPatterns = URLData.getAllProductControllerURL();
        registry.addInterceptor(productControllerInterceptor())
                .addPathPatterns(pathPatterns);
    }

    public void addOtherDataInterceptors(InterceptorRegistry registry) {
        List<String> pathPatterns = URLData.getAllOtherDataControllerURL();
        registry.addInterceptor(otherDataControllerInterceptor())
                .addPathPatterns(pathPatterns);
    }

    public void addUserInterceptors(InterceptorRegistry registry) {
        List<String> pathPatterns = URLData.getAllUserControllerURL();
        registry.addInterceptor(userControllerInterceptor())
                .addPathPatterns(pathPatterns);
    }
}
