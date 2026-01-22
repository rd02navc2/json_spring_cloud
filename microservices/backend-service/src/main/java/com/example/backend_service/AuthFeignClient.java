package com.example.backend_service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.auth_service.dto.AuthVerifyRequest;
import com.example.auth_service.dto.AuthVerifyResponse;

@FeignClient(
        name = "auth-service",
        path = "/auth",
        fallback = AuthFeignFallback.class   // ⚡ 指定 fallback
)
public interface AuthFeignClient {

    @PostMapping("/verify")
    AuthVerifyResponse verify(@RequestBody AuthVerifyRequest request);
}
