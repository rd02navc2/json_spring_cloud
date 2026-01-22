package com.example.auth_service.controller;

import com.example.auth_service.dto.*;
import com.example.auth_service.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * 登入取得 Token
     */
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        String token = authService.login(request.getUsername(), request.getPassword());

        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new LoginResponse(false, null, "Invalid credentials"));
        }

        return ResponseEntity.ok(
                new LoginResponse(true, token, "Login successful")
        );
    }

    /**
     * 驗證 Token
     */
    @PostMapping("/verify")
    public ResponseEntity<AuthVerifyResponse> verify(@RequestBody AuthVerifyRequest request) {
        AuthService.AuthTokenInfo tokenInfo = authService.getTokenInfo(request.getToken());

        if (tokenInfo == null) {
            // Token 無效或過期
            return new ResponseEntity<>(
                    new AuthVerifyResponse(false, 
                    		"Invalid or expired token",
                    		tokenInfo.getUsername(),
                            tokenInfo.getUserId()),
                    HttpStatus.OK
            );
        }

        // Token 驗證成功，返回用戶資訊
        return new ResponseEntity<>(
                new AuthVerifyResponse(
                        true,
                        "Token valid",
                        tokenInfo.getUsername(),
                        tokenInfo.getUserId()
                ),
                HttpStatus.OK
        );
    }
}
