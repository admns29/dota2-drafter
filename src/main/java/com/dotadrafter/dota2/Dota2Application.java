package com.dotadrafter.dota2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Main entry point for the Dota 2 Draft Picker application
// Spring Boot auto-configures the application with web, data, and JPA support
@SpringBootApplication
public class Dota2Application {

	public static void main(String[] args) {
		SpringApplication.run(Dota2Application.class, args);
	}

}
