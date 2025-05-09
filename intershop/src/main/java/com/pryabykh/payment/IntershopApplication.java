package com.pryabykh.payment;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class IntershopApplication {

	public static void main(String[] args) {
		SpringApplication.run(IntershopApplication.class, args);
	}

}
