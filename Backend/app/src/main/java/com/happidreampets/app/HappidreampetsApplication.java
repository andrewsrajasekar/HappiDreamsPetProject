package com.happidreampets.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@ServletComponentScan
@EnableWebMvc
// @ComponentScan(basePackages = "com.happidreampets.app.database.repository")
@Import(FilterConfig.class)
public class HappidreampetsApplication extends WebMvcAutoConfiguration {

	public static void main(String[] args) {
		SpringApplication.run(HappidreampetsApplication.class, args);
	}

	@Bean
	public WebMvcConfigurer configure() {
		return new WebMvcConfigurer() {
			@Override
			public void addCorsMappings(CorsRegistry reg) {
				reg.addMapping("/*").allowedOrigins("http://localhost:5173").allowedMethods("*").allowedHeaders("*");
			}

			@Override
			public void addResourceHandlers(ResourceHandlerRegistry registry) {
				registry
						.addResourceHandler("/static/**")
						.addResourceLocations("classpath:/static/");
			}
		};
	}
}
