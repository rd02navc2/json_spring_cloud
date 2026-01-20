package com.example.auth_service.dto;

public class AuthVerifyRequest {

    private String token;

    public AuthVerifyRequest() {
        // Jackson 反序列化需要
    }
    
    public AuthVerifyRequest(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
