package com.cognizant.taxpayerService.client;

import com.cognizant.taxpayerService.util.AuthUtil;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Component
public class FeignClientInterceptor implements RequestInterceptor {

    @Autowired
    private AuthUtil jwtUtil;

    @Override
    public void apply(RequestTemplate template) {
        // Generate a token specifically with the INTERNAL role
        String internalToken = jwtUtil.generateToken("SYSTEM-GATEWAY", "INTERNAL");
        template.header("Authorization", "Bearer " + internalToken);
    }
}