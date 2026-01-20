package com.example.card_service.service;

import com.example.card_service.entity.Card;
import com.example.card_service.repository.CardRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
public class CardService {
    
    private final CardRepository cardRepository;
    
    public CardService(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }
    
    @Transactional
    public boolean processPayment(String cardNo, int amount) {
        Optional<Card> cardOpt = cardRepository.findByCardNo(cardNo);
        
        if (cardOpt.isEmpty()) {
            return false;
        }
        
        Card card = cardOpt.get();
        if (!card.canPay(amount)) {
            return false;
        }
        
        card.deduct(amount);
        cardRepository.save(card);
        return true;
    }
}