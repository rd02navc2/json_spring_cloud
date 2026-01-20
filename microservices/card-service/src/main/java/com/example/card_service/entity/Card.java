package com.example.card_service.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "cards")
public class Card {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String cardNo;
    
    @Column(nullable = false)
    private String cardHolder;
    
    @Column(nullable = false)
    private BigDecimal balance;
    
    @Column(nullable = false)
    private boolean active = true;
    
    // Constructors
    public Card() {}
    
    public Card(String cardNo, String cardHolder, BigDecimal balance) {
        this.cardNo = cardNo;
        this.cardHolder = cardHolder;
        this.balance = balance;
    }
    
    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getCardNo() { return cardNo; }
    public void setCardNo(String cardNo) { this.cardNo = cardNo; }
    
    public String getCardHolder() { return cardHolder; }
    public void setCardHolder(String cardHolder) { this.cardHolder = cardHolder; }
    
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    
    public boolean canPay(int amount) {
        return active && balance.compareTo(BigDecimal.valueOf(amount)) >= 0;
    }
    
    public void deduct(int amount) {
        this.balance = balance.subtract(BigDecimal.valueOf(amount));
    }
}