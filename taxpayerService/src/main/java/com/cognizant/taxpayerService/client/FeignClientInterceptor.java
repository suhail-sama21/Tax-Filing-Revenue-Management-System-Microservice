package com.cognizant.taxpayerService.client;

import com.cognizant.taxpayerService.util.AuthUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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