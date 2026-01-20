package com.example.auth_service.dto;

public class AuthVerifyResponse {
    private boolean valid;
    private String message;  // ⚠️ 加上這個欄位

    public AuthVerifyResponse() {}

    public AuthVerifyResponse(boolean valid, String message) {
        this.valid = valid;
        this.message = message;
    }

    // Getters & Setters
    public boolean isValid() { 
        return valid; 
    }
    
    public void setValid(boolean valid) { 
        this.valid = valid; 
    }
    
    public String getMessage() { 
        return message; 
    }
    
    public void setMessage(String message) { 
        this.message = message; 
    }
}