package com.happidreampets.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
// @ComponentScan(basePackages = "com.happidreampets.app.database.repository")
public class HappidreampetsApplication {

	public static void main(String[] args) {
		SpringApplication.run(HappidreampetsApplication.class, args);
	}

}
