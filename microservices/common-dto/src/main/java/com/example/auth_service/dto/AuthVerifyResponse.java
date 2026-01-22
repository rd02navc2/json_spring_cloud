package com.example.auth_service.dto;

public class AuthVerifyResponse {
    private boolean valid;
    private String message;
    private String errorcode;
    private String username;
    private Long userId;

    // 無參數建構子
    public AuthVerifyResponse() {}

    // 四個欄位建構子
    public AuthVerifyResponse(boolean valid, String message, String username, Long userId) {
        this.valid = valid;
        this.message = message;
        this.username = username;
        this.userId = userId;
    }

    // Getters & Setters
    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    
    public String getErrorCode() { return errorcode; }
    public void setErrorCode(String errorcode) { this.errorcode = errorcode; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
}
