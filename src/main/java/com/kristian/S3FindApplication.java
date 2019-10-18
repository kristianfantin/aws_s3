package com.kristian;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("com.kristian")
@EnableAutoConfiguration
public class S3FindApplication {

	public static void main(String[] args) {
		SpringApplication.run(S3FindApplication.class, args);
	}

}
