package com.cognizant.paymentService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients; // <-- Import this!

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.cognizant.paymentService.client") // <-- ADD THIS ANNOTATION!
public class PaymentServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymentServiceApplication.class, args);
	}
}