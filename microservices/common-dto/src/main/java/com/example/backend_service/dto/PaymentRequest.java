package com.example.backend_service.dto;

public class PaymentRequest {

    private String token;
    private String cardNo;
    private int amount;

    public PaymentRequest() {
        // Spring 反序列化 JSON 一定需要無參數建構子
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }
}
