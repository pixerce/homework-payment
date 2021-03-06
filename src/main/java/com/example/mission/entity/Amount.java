package com.example.mission.entity;

import com.example.mission.payment.model.BaseModelSupport;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class Amount extends BaseModelSupport {

    @Column(name="amount")
    private long amount;
    @Column(name="vat")
    private long vat;

    public Amount(long amount, Long vat) {
        this.amount = amount;
        this.vat = (vat == null) ? calculateVat(amount) : vat;
    }

    public static Amount of(long amount, Long vat) {
        return new Amount(amount, vat);
    }

    private static long calculateVat(long amount) {
        final BigDecimal vatPercentage = BigDecimal.valueOf(11);
        return BigDecimal.valueOf(amount).divide(vatPercentage, 1, BigDecimal.ROUND_CEILING).longValue();
    }
}
