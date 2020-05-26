package com.example.mission.payment.crypto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
public class CryptionServiceTest {

    @Autowired
    protected CipherHandler cryptionService;

    @Test
    void shouldEncrypt() {
        final String originText = "1234567890123456|0820|202";

        final String encyrpted = this.cryptionService.encrypt(originText);
        final String decrypted = this.cryptionService.decrypt(encyrpted);

        assertEquals(originText, decrypted);
    }
}
