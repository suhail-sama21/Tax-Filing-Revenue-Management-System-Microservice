package com.cognizant.taxease.reportingService.client;

import com.cognizant.taxease.reportingService.util.AuthUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class FeignClientInterceptor implements RequestInterceptor {

    @Value("${internal.auth.header-name}")
    private String headerName;

    @Value("${internal.auth.secret-value}")
    private String secretValue;

    @Override
    public void apply(RequestTemplate template) {
        // Efficiently injects the static key-value pair
        template.header(headerName, secretValue);
    }
}