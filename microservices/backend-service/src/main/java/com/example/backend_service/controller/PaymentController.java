package com.example.backend_service.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.example.backend_service.AuthFeignClient;
import com.example.backend_service.CardFeignClient;
import com.example.backend_service.PaymentResultMapper;
import com.example.backend_service.dto.PaymentRequest;
import com.example.backend_service.dto.PaymentResult;
import com.example.auth_service.dto.AuthVerifyResponse;
import com.example.auth_service.dto.AuthVerifyRequest;
import com.example.card_service.dto.CardPayResponse;
import com.example.card_service.dto.CardPayRequest;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final AuthFeignClient authFeignClient;
    private final CardFeignClient cardFeignClient;

    public PaymentController(AuthFeignClient authFeignClient,
                             CardFeignClient cardFeignClient) {
        this.authFeignClient = authFeignClient;
        this.cardFeignClient = cardFeignClient;
    }

    @PostMapping("/pay")
    public ResponseEntity<PaymentResult> pay(@RequestBody PaymentRequest request) {

        // ===== 1️⃣ 驗證 Token =====
        AuthVerifyResponse authResp = authFeignClient.verify(new AuthVerifyRequest(request.getToken()));
        PaymentResult authResult = PaymentResultMapper.fromAuth(authResp);
        if (!authResult.isAuthValid()) { // authResult.isAuthValid() = false → 授權失敗
            return ResponseEntity.status(authResult.getErrorcode().contains("SERVICE") ? 502 : 401)
                    .body(authResult);
        }

        // ===== 2️⃣ 呼叫 Card Service =====
        CardPayResponse cardResp = cardFeignClient.pay(new CardPayRequest(request.getCardNo(), request.getAmount()));
        PaymentResult cardResult = PaymentResultMapper.fromCard(cardResp);

        // ===== 3️⃣ 回傳整合結果 =====
        return cardResult.getErrorcode().contains("SERVICE")
                ? ResponseEntity.status(502).body(cardResult)
                : ResponseEntity.ok(cardResult);
    }
}


