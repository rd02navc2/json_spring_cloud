package com.example.card_service.dto;

public class CardPayResponse {

    private boolean success;
    private String cardNo;
    private int amount;
    private String message;

    // 帶參數建構子
    public CardPayResponse(boolean success, String cardNo, int amount, String message) {
        this.success = success;
        this.cardNo = cardNo;
        this.amount = amount;
        this.message = message;
    }

    // 空建構子 (JSON 反序列化需要)
    public CardPayResponse() {}

    // ===== Getter =====
    public boolean isSuccess() {
        return success;
    }

    public String getCardNo() {
        return cardNo;
    }

    public int getAmount() {
        return amount;
    }

    public String getMessage() {
        return message;
    }

    // ===== Setter =====
    public void setSuccess(boolean success) {
        this.success = success;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
