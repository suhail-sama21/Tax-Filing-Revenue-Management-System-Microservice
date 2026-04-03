package com.example.revenueService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class RevenueServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(RevenueServiceApplication.class, args);
	}

}
