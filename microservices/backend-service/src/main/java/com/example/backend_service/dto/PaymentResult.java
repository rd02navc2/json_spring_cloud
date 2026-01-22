package com.example.backend_service.dto;

public class PaymentResult {

    private boolean authValid;
    private boolean paymentSuccess;
    private String message;
    private String errorcode;


    public PaymentResult(boolean authValid, boolean paymentSuccess, String message) {
        this.authValid = authValid;
        this.paymentSuccess = paymentSuccess;
        this.message = message;
        
    }

    public boolean isAuthValid() {
        return authValid;
    }

    public boolean isPaymentSuccess() {
        return paymentSuccess;
    }

    public String getMessage() {
        return message;
    }
    
    public String getErrorcode() {
        return errorcode;
    }


}
