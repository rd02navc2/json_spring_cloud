package com.example.backend_service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.example.card_service.dto.CardPayRequest;
import com.example.card_service.dto.CardPayResponse;

@FeignClient(
        name = "card-service",
        path = "/card"
)
public interface CardFeignClient {

    @PostMapping("/pay")
    CardPayResponse pay(@RequestBody CardPayRequest request);
}
