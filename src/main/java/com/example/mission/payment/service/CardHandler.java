package com.example.mission.payment.service;

import com.example.mission.payment.repository.entity.Card;
import com.example.mission.payment.crypto.CipherHandler;
import com.example.mission.payment.repository.CardRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.example.mission.payment.repository.entity.Card.hash;

@Service
public class CardHandler {

    private final CardRepository cardRepository;

    private CipherHandler cipher;

    public CardHandler(CardRepository cardRepository, CipherHandler cipher) {
        this.cardRepository = cardRepository;
        this.cipher = cipher;
    }

    final public Card make(String cardNumber, String validDate, String cvc) {
        final String cardInfo = Arrays.asList(cardNumber, validDate, cvc).stream().collect(Collectors.joining("|"));
        final String hashKey = hash(cardInfo);

        Optional<Card> card = Optional.ofNullable(this.cardRepository.findByHashKey(hashKey));
        if (!card.isPresent()) {
            final String encryptedCardInfo = this.cipher.encrypt(cardInfo);
            return Card.builder().hashKey(hashKey).info(encryptedCardInfo).build();
        } else {
            return card.get();
        }
    }

    public Card decrypt(Card card) {
        String cardInfo = cipher.decrypt(card.getInfo());

        String [] cardTokens = StringUtils.split(cardInfo, "|");

        card.setCardNumber(cardTokens[0]);
        card.setValidDate(cardTokens[1]);
        card.setCvc(cardTokens[2]);
        return card;
    }


}
