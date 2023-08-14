package com.happidreampets.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
@ServletComponentScan
// @ComponentScan(basePackages = "com.happidreampets.app.database.repository")
@Import(FilterConfig.class)
public class HappidreampetsApplication {

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
		};
	}

}
