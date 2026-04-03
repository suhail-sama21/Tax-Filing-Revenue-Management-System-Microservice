package com.cognizant.auditservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@EnableDiscoveryClient
@SpringBootApplication
public class AuditserviceApplication {

	public static void main(String[] args) {
		SpringApplication.run(AuditserviceApplication.class, args);
	}

}
