package com.example.fooddonation;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;


@SpringBootApplication
@EnableAsync
public class FoodDonationApplication {

	public static void main(String[] args) {
		SpringApplication.run(FoodDonationApplication.class, args);
	}

}
