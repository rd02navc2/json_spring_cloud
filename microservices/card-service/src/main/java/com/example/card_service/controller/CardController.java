package com.example.card_service.controller;

import org.springframework.web.bind.annotation.*;
import com.example.card_service.dto.CardPayRequest;
import com.example.card_service.dto.CardPayResponse;
import com.example.card_service.service.CardService;

@RestController
@RequestMapping("/card")
public class CardController {
    
    private final CardService cardService;
    
    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping("/pay")
    public CardPayResponse pay(@RequestBody CardPayRequest request) {
        boolean success = cardService.processPayment(
            request.getCardNo(), 
            request.getAmount()
        );
        
        return new CardPayResponse(success, request.getCardNo(), request.getAmount(), null, null);
    }
}