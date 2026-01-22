package com.example.backend_service;

import org.springframework.stereotype.Component;

import com.example.auth_service.dto.AuthVerifyRequest;
import com.example.auth_service.dto.AuthVerifyResponse;

@Component
public class AuthFeignFallback implements AuthFeignClient {

    @Override
    public AuthVerifyResponse verify(AuthVerifyRequest request) {
        AuthVerifyResponse resp = new AuthVerifyResponse();
        resp.setValid(false);
        resp.setErrorCode("AUTH_SERVICE_UNAVAILABLE");
        return resp;
    }
}
