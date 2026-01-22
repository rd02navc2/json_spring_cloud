package com.example.backend_service;

import com.example.auth_service.dto.AuthVerifyResponse;
import com.example.backend_service.dto.PaymentResult;
import com.example.card_service.dto.CardPayResponse;

public class PaymentResultMapper {

    // 解析 AuthVerifyResponse
    public static PaymentResult fromAuth(AuthVerifyResponse authResp) {
        switch (authResp.getErrorCode()) {
            case "TOKEN_INVALID": return new PaymentResult(false, false, "INVALID_TOKEN");
            case "TOKEN_EXPIRED": return new PaymentResult(false, false, "TOKEN_EXPIRED");
            case "SERVICE_UNAVAILABLE": return new PaymentResult(false, false, "AUTH_SERVICE_UNAVAILABLE");
            default:
                return authResp.isValid()
                        ? new PaymentResult(true, true, "AUTH_OK")
                        : new PaymentResult(false, false, "AUTH_FAILED");
        }
    }

    // 解析 CardPayResponse
    public static PaymentResult fromCard(CardPayResponse cardResp) {
        switch (cardResp.getErrorCode()) {
            case "INSUFFICIENT_FUNDS": return new PaymentResult(true, false, "INSUFFICIENT_FUNDS");
            case "CARD_INVALID": return new PaymentResult(true, false, "CARD_INVALID");
            case "SERVICE_UNAVAILABLE": return new PaymentResult(true, false, "CARD_SERVICE_UNAVAILABLE");
            default:
                return cardResp.isSuccess()
                        ? new PaymentResult(true, true, cardResp.getMessage())
                        : new PaymentResult(true, false, cardResp.getMessage());
        }
    }
}
