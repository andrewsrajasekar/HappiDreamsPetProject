package com.happidreampets.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@ServletComponentScan
// @ComponentScan(basePackages = "com.happidreampets.app.database.repository")
@Import(FilterConfig.class)
public class HappidreampetsApplication {

	public static void main(String[] args) {
		SpringApplication.run(HappidreampetsApplication.class, args);
	}

}
