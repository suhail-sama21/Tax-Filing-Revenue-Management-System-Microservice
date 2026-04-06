package com.cognizant.taxpayerService.dto;

import com.cognizant.taxpayerService.dto.UserDto;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaxpayerResponse {
    private Long taxpayerId;
    private String taxpayerIdNumber;
    private String type;


    // This will hold the data fetched from the User Service!
    private UserDto user;
}