package com.example.mission.payment.crypto;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.Base64Utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;

@Service
public class CipherHandler {

    @Value("${cryption.key}")
    private String key;

    @Value("${cryption.iv}")
    private String initVector;

    public String encrypt(String data) {
        return AES256Cipher.encrypt(data, key, initVector);
    }

    public String decrypt(String data) {
        return AES256Cipher.decrypt(data, key, initVector);
    }


    static class AES256Cipher {

        private static final String ENCODE_UTF_8 = "UTF-8";
        private static final String ALGORITHM = "AES";

        public static String generateKey() throws NoSuchAlgorithmException {
            KeyGenerator generator = KeyGenerator.getInstance(ALGORITHM);
            generator.init(256);    // AES256, iv를 생성할 때는 128로 변경한다.

            SecretKey secretKey = generator.generateKey();

            byte[] keyBytes = secretKey.getEncoded();
            return Base64Utils.encodeToString(keyBytes);
        }

        public static String encrypt(String value, final String key, final String initVector) {
            try {
                IvParameterSpec iv = new IvParameterSpec(Base64Utils.decode(initVector.getBytes()));
                SecretKeySpec secretKeySpec = new SecretKeySpec(Base64Utils.decode(key.getBytes()), ALGORITHM);

                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
                cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);

                String v = Base64Utils.encodeToString(value.getBytes(ENCODE_UTF_8));
                byte[] encrypted = cipher.doFinal(v.getBytes(ENCODE_UTF_8));
                return Base64Utils.encodeToString(encrypted);
            } catch (Exception e) {
                return null;
            }
        }

        public static String decrypt(String str, final String key, final String initVector) {
            try {
                byte[] encryptedMsg = Base64Utils.decode(str.getBytes(ENCODE_UTF_8));

                SecretKeySpec secretKeySpec = new SecretKeySpec(Base64Utils.decode(key.getBytes()), ALGORITHM);
                IvParameterSpec iv = new IvParameterSpec(Base64Utils.decode(initVector.getBytes()));

                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);

                byte[] decryptedMsg = cipher.doFinal(encryptedMsg);
                return new String(Base64Utils.decode(decryptedMsg), ENCODE_UTF_8);
            } catch (GeneralSecurityException | UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }
    }


}
