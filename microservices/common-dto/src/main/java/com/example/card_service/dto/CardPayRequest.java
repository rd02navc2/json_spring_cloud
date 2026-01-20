package com.example.card_service.dto;

public class CardPayRequest {

    private String cardNo;
    private int amount;

    public CardPayRequest() {
        // Jackson 反序列化需要
    }

    public CardPayRequest(String cardNo, int amount) {
        this.cardNo = cardNo;
        this.amount = amount;
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
