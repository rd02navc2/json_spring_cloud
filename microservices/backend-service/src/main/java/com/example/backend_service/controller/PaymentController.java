package com.example.backend_service.controller;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import com.example.backend_service.dto.PaymentRequest;
import com.example.backend_service.dto.PaymentResult;
import com.example.auth_service.dto.AuthVerifyResponse;
import com.example.auth_service.dto.AuthVerifyRequest;
import com.example.card_service.dto.CardPayResponse;
import com.example.card_service.dto.CardPayRequest;

@RestController
@RequestMapping("/api")
public class PaymentController {

    private final RestTemplate restTemplate;

    public PaymentController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping("/pay")
    public ResponseEntity<PaymentResult> pay(@RequestBody PaymentRequest request) {

        String token = request.getToken();
        String cardNo = request.getCardNo();
        int amount = request.getAmount();
        
     // ===== 建立通用 JSON Header =====
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

     // ===== 1️⃣ 呼叫 Auth Service =====
        HttpEntity<AuthVerifyRequest> authEntity =
                new HttpEntity<>(new AuthVerifyRequest(token), headers);
        AuthVerifyResponse authResp;
        try {
            authResp = restTemplate.postForObject(
                    "http://auth-service/auth/verify",
                    authEntity,
                    AuthVerifyResponse.class
            );
        } catch (Exception e) {
            // 服務連線失敗
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(new PaymentResult(false, false, "AUTH_SERVICE_UNAVAILABLE"));
        }

        // Auth Service 正常回應,但驗證失敗
        if (authResp == null || !authResp.isValid()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new PaymentResult(false, false, "INVALID_TOKEN"));
        }
        
        // ===== 2️⃣ 呼叫 Card Service =====
        HttpEntity<CardPayRequest> cardEntity =
                new HttpEntity<>(new CardPayRequest(cardNo, amount), headers);

        CardPayResponse cardResp;
        try {
            cardResp = restTemplate.postForObject(
                    "http://card-service/card/pay",
                    cardEntity,
                    CardPayResponse.class
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(new PaymentResult(true, false, "CARD SERVICE ERROR"));
        }

        if (cardResp == null) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(new PaymentResult(true, false, "CARD SERVICE ERROR"));
        }

        // ===== 3️⃣ 回傳整合結果 =====
        return ResponseEntity.ok(
                new PaymentResult(true, cardResp.isSuccess(), cardResp.getMessage())
        );
    }
}


