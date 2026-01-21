package com.example.backend_service.controller;

import com.example.auth_service.dto.AuthVerifyRequest;
import com.example.auth_service.dto.AuthVerifyResponse;
import com.example.backend_service.dto.PaymentRequest;
import com.example.backend_service.dto.PaymentResult;
import com.example.card_service.dto.CardPayRequest;
import com.example.card_service.dto.CardPayResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Payment Controller 測試")
class PaymentControllerTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private PaymentController paymentController;

    private PaymentRequest paymentRequest;
    private AuthVerifyResponse authValidResponse;
    private AuthVerifyResponse authInvalidResponse;
    private CardPayResponse cardSuccessResponse;
    private CardPayResponse cardFailResponse;

    @BeforeEach
    void setUp() {
        // 準備測試資料
        paymentRequest = new PaymentRequest();
        paymentRequest.setToken("valid-token-123");
        paymentRequest.setCardNo("1234-5678-9012-3456");
        paymentRequest.setAmount(1000);

        // Auth Service 有效回應
        authValidResponse = new AuthVerifyResponse();
        authValidResponse.setValid(true);
        authValidResponse.setMessage("Token valid");

        // Auth Service 無效回應
        authInvalidResponse = new AuthVerifyResponse();
        authInvalidResponse.setValid(false);
        authInvalidResponse.setMessage("Token invalid");

        // Card Service 成功回應
        cardSuccessResponse = new CardPayResponse();
        cardSuccessResponse.setSuccess(true);
        cardSuccessResponse.setMessage("payment successful");

        // Card Service 失敗回應
        cardFailResponse = new CardPayResponse();
        cardFailResponse.setSuccess(false);
        cardFailResponse.setMessage("insufficient balance");
    }

    @Test
    @DisplayName("1. 支付成功 - Token 有效且餘額足夠")
    void testPaymentSuccess() {
        // Given: Mock Auth Service 回應 token 有效
        when(restTemplate.postForObject(
                eq("http://auth-service/auth/verify"),
                any(HttpEntity.class),
                eq(AuthVerifyResponse.class)
        )).thenReturn(authValidResponse);

        // Mock Card Service 回應支付成功
        when(restTemplate.postForObject(
                eq("http://card-service/card/pay"),
                any(HttpEntity.class),
                eq(CardPayResponse.class)
        )).thenReturn(cardSuccessResponse);

        // When: 呼叫支付 API
        ResponseEntity<PaymentResult> response = paymentController.pay(paymentRequest);

        // Then: 驗證結果
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        PaymentResult result = response.getBody();
        assertNotNull(result);
        assertTrue(result.isAuthValid());
        assertTrue(result.isPaymentSuccess());
        assertEquals("payment successful", result.getMessage());

        // 驗證服務呼叫次數
        verify(restTemplate, times(1)).postForObject(
                eq("http://auth-service/auth/verify"),
                any(HttpEntity.class),
                eq(AuthVerifyResponse.class)
        );
        verify(restTemplate, times(1)).postForObject(
                eq("http://card-service/card/pay"),
                any(HttpEntity.class),
                eq(CardPayResponse.class)
        );
    }

    @Test
    @DisplayName("2. Token 無效 - Auth Service 回應 invalid")
    void testInvalidToken() {
        // Given: Mock Auth Service 回應 token 無效
        when(restTemplate.postForObject(
                eq("http://auth-service/auth/verify"),
                any(HttpEntity.class),
                eq(AuthVerifyResponse.class)
        )).thenReturn(authInvalidResponse);

        // When: 呼叫支付 API
        ResponseEntity<PaymentResult> response = paymentController.pay(paymentRequest);

        // Then: 驗證結果
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        
        PaymentResult result = response.getBody();
        assertNotNull(result);
        assertFalse(result.isAuthValid());
        assertFalse(result.isPaymentSuccess());
        assertEquals("INVALID_TOKEN", result.getMessage());

        // 驗證只呼叫了 Auth Service,沒有呼叫 Card Service
        verify(restTemplate, times(1)).postForObject(
                eq("http://auth-service/auth/verify"),
                any(HttpEntity.class),
                eq(AuthVerifyResponse.class)
        );
        verify(restTemplate, never()).postForObject(
                eq("http://card-service/card/pay"),
                any(HttpEntity.class),
                eq(CardPayResponse.class)
        );
    }

    @Test
    @DisplayName("3. Token 驗證通過但餘額不足")
    void testInsufficientBalance() {
        // Given: Mock Auth Service 回應 token 有效
        when(restTemplate.postForObject(
                eq("http://auth-service/auth/verify"),
                any(HttpEntity.class),
                eq(AuthVerifyResponse.class)
        )).thenReturn(authValidResponse);

        // Mock Card Service 回應餘額不足
        when(restTemplate.postForObject(
                eq("http://card-service/card/pay"),
                any(HttpEntity.class),
                eq(CardPayResponse.class)
        )).thenReturn(cardFailResponse);

        // When: 呼叫支付 API
        ResponseEntity<PaymentResult> response = paymentController.pay(paymentRequest);

        // Then: 驗證結果
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        
        PaymentResult result = response.getBody();
        assertNotNull(result);
        assertTrue(result.isAuthValid());
        assertFalse(result.isPaymentSuccess());
        assertEquals("insufficient balance", result.getMessage());

        // 驗證兩個服務都被呼叫了
        verify(restTemplate, times(1)).postForObject(
                eq("http://auth-service/auth/verify"),
                any(HttpEntity.class),
                eq(AuthVerifyResponse.class)
        );
        verify(restTemplate, times(1)).postForObject(
                eq("http://card-service/card/pay"),
                any(HttpEntity.class),
                eq(CardPayResponse.class)
        );
    }

    @Test
    @DisplayName("4. Auth Service 無法連線")
    void testAuthServiceUnavailable() {
        // Given: Mock Auth Service 拋出例外
        when(restTemplate.postForObject(
                eq("http://auth-service/auth/verify"),
                any(HttpEntity.class),
                eq(AuthVerifyResponse.class)
        )).thenThrow(new RestClientException("Connection refused"));

        // When: 呼叫支付 API
        ResponseEntity<PaymentResult> response = paymentController.pay(paymentRequest);

        // Then: 驗證結果
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
        
        PaymentResult result = response.getBody();
        assertNotNull(result);
        assertFalse(result.isAuthValid());
        assertFalse(result.isPaymentSuccess());
        assertEquals("AUTH_SERVICE_UNAVAILABLE", result.getMessage());

        // 驗證只嘗試呼叫 Auth Service
        verify(restTemplate, times(1)).postForObject(
                eq("http://auth-service/auth/verify"),
                any(HttpEntity.class),
                eq(AuthVerifyResponse.class)
        );
        verify(restTemplate, never()).postForObject(
                eq("http://card-service/card/pay"),
                any(HttpEntity.class),
                eq(CardPayResponse.class)
        );
    }

    @Test
    @DisplayName("5. Card Service 無法連線")
    void testCardServiceUnavailable() {
        // Given: Mock Auth Service 回應成功
        when(restTemplate.postForObject(
                eq("http://auth-service/auth/verify"),
                any(HttpEntity.class),
                eq(AuthVerifyResponse.class)
        )).thenReturn(authValidResponse);

        // Mock Card Service 拋出例外
        when(restTemplate.postForObject(
                eq("http://card-service/card/pay"),
                any(HttpEntity.class),
                eq(CardPayResponse.class)
        )).thenThrow(new RestClientException("Connection timeout"));

        // When: 呼叫支付 API
        ResponseEntity<PaymentResult> response = paymentController.pay(paymentRequest);

        // Then: 驗證結果
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
        
        PaymentResult result = response.getBody();
        assertNotNull(result);
        assertTrue(result.isAuthValid());
        assertFalse(result.isPaymentSuccess());
        assertEquals("CARD SERVICE ERROR", result.getMessage());

        // 驗證兩個服務都被呼叫了
        verify(restTemplate, times(1)).postForObject(
                eq("http://auth-service/auth/verify"),
                any(HttpEntity.class),
                eq(AuthVerifyResponse.class)
        );
        verify(restTemplate, times(1)).postForObject(
                eq("http://card-service/card/pay"),
                any(HttpEntity.class),
                eq(CardPayResponse.class)
        );
    }

    @Test
    @DisplayName("6. Auth Service 回應 null")
    void testAuthServiceReturnsNull() {
        // Given: Mock Auth Service 回應 null
        when(restTemplate.postForObject(
                eq("http://auth-service/auth/verify"),
                any(HttpEntity.class),
                eq(AuthVerifyResponse.class)
        )).thenReturn(null);

        // When: 呼叫支付 API
        ResponseEntity<PaymentResult> response = paymentController.pay(paymentRequest);

        // Then: 驗證結果
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        
        PaymentResult result = response.getBody();
        assertNotNull(result);
        assertFalse(result.isAuthValid());
        assertFalse(result.isPaymentSuccess());
        assertEquals("INVALID_TOKEN", result.getMessage());
    }

    @Test
    @DisplayName("7. Card Service 回應 null")
    void testCardServiceReturnsNull() {
        // Given: Mock Auth Service 回應成功
        when(restTemplate.postForObject(
                eq("http://auth-service/auth/verify"),
                any(HttpEntity.class),
                eq(AuthVerifyResponse.class)
        )).thenReturn(authValidResponse);

        // Mock Card Service 回應 null
        when(restTemplate.postForObject(
                eq("http://card-service/card/pay"),
                any(HttpEntity.class),
                eq(CardPayResponse.class)
        )).thenReturn(null);

        // When: 呼叫支付 API
        ResponseEntity<PaymentResult> response = paymentController.pay(paymentRequest);

        // Then: 驗證結果
        assertNotNull(response);
        assertEquals(HttpStatus.BAD_GATEWAY, response.getStatusCode());
        
        PaymentResult result = response.getBody();
        assertNotNull(result);
        assertTrue(result.isAuthValid());
        assertFalse(result.isPaymentSuccess());
        assertEquals("CARD SERVICE ERROR", result.getMessage());
    }

    @Test
    @DisplayName("8. 測試不同金額的支付")
    void testDifferentAmounts() {
        // Given: Mock 服務回應
        when(restTemplate.postForObject(
                eq("http://auth-service/auth/verify"),
                any(HttpEntity.class),
                eq(AuthVerifyResponse.class)
        )).thenReturn(authValidResponse);

        when(restTemplate.postForObject(
                eq("http://card-service/card/pay"),
                any(HttpEntity.class),
                eq(CardPayResponse.class)
        )).thenReturn(cardSuccessResponse);

        // When & Then: 測試不同金額
        int[] amounts = {100, 1000, 10000, 99999};
        
        for (int amount : amounts) {
            paymentRequest.setAmount(amount);
            ResponseEntity<PaymentResult> response = paymentController.pay(paymentRequest);
            
            assertNotNull(response);
            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(response.getBody().isPaymentSuccess());
        }

        // 驗證呼叫次數
        verify(restTemplate, times(amounts.length)).postForObject(
                eq("http://auth-service/auth/verify"),
                any(HttpEntity.class),
                eq(AuthVerifyResponse.class)
        );
    }

    @Test
    @DisplayName("9. 測試空值輸入")
    void testNullInputs() {
        // Given: 建立空值請求
        PaymentRequest nullRequest = new PaymentRequest();
        nullRequest.setToken(null);
        nullRequest.setCardNo(null);
        nullRequest.setAmount(0);

        // Mock Auth Service 回應
        when(restTemplate.postForObject(
                eq("http://auth-service/auth/verify"),
                any(HttpEntity.class),
                eq(AuthVerifyResponse.class)
        )).thenReturn(authInvalidResponse);

        // When: 呼叫支付 API
        ResponseEntity<PaymentResult> response = paymentController.pay(nullRequest);

        // Then: 應該被 Auth Service 拒絕
        assertNotNull(response);
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
        assertFalse(response.getBody().isAuthValid());
    }
}