package com.cognizant.taxpayerService;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class TaxpayerService {

    public static void main(String[] args) {
        SpringApplication.run(TaxpayerService.class, args);
    }

}