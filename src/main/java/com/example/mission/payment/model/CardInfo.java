package com.example.mission.payment.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

@Setter
@Getter
public class CardInfo {

    private String cardNumber;
    private String validDate;
    private String cvc;

    public static CardInfo with(String cardInfo) {
        String [] cardTokens = StringUtils.split(cardInfo, "|");

        CardInfo card = new CardInfo();
        card.setCardNumber(cardTokens[0]);
        card.setValidDate(cardTokens[1]);
        card.setCvc(cardTokens[2]);
        return card;
    }
}
