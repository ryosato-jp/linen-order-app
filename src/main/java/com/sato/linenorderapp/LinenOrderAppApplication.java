package com.sato.linenorderapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan("com.sato.linenorderapp.entity")
public class LinenOrderAppApplication {
	public static void main(String[] args) {
		SpringApplication.run(LinenOrderAppApplication.class, args);
	}
}