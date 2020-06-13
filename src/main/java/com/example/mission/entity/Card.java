package com.example.mission.entity;

import com.google.common.hash.Hashing;
import lombok.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.nio.charset.StandardCharsets;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Setter
@Getter
@Table(name = "card")
public class Card extends BaseEntity {

    public final static Card NONE = null;

    @Column(name = "hash_key")
    private String hashKey;

    @Column(name = "info")
    private String info;

    @Transient
    private String cardNumber;
    @Transient
    private String validDate;
    @Transient
    private String cvc;

    public String getHashKey() {
        return hashKey;
    }

    public void setHashKey(String hashKey) {
        this.hashKey = hashKey;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public static String masking(String number) {
        if (StringUtils.isBlank(number))
            return "";

        final int FIRST_6_DIGIT_NONE_MASKING = 6;
        final int LAST_3_DIGIT_NONE_MASKING = 3;

        int length = number.length();
        final String first = StringUtils.substring(number, 0,  FIRST_6_DIGIT_NONE_MASKING);
        return new StringBuilder()
                .append(first)
                .append(StringUtils.repeat("*", length - FIRST_6_DIGIT_NONE_MASKING - LAST_3_DIGIT_NONE_MASKING))
                .append(StringUtils.substring(number, length - LAST_3_DIGIT_NONE_MASKING))
                .toString();
    }

    public static String hash(String data) {
        return Hashing.sha256()
                .hashString(data, StandardCharsets.UTF_8)
                .toString();
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.DEFAULT_STYLE);
    }
}
