package com.example.mission.payment.message.service;

import com.example.mission.payment.crypto.CipherHandler;
import com.example.mission.payment.message.annotation.DigitField;
import com.example.mission.payment.message.annotation.EncryptField;
import com.example.mission.payment.message.annotation.Order;
import com.example.mission.payment.message.annotation.TextField;
import com.example.mission.payment.message.model.Message;
import com.example.mission.payment.message.type.Padding;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.LinkedList;
import java.util.Optional;


@Service
public class MessageWriterService {

    private CipherHandler cipher;

    public MessageWriterService(CipherHandler cipher) {
        this.cipher = cipher;
    }

    public String writeAsString(Message message) {
        Class<?> clazz = message.getClass();

        LinkedList<String> tokens = new LinkedList<>();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);

            String str = String.valueOf(Optional.ofNullable(ReflectionUtils.getField(field, message)).orElse(""));

            if (field.isAnnotationPresent(EncryptField.class)) {
                str = cipher.encrypt(str);
            }

            if (field.isAnnotationPresent(DigitField.class)) {
                DigitField annotation = field.getAnnotation(DigitField.class);
                str = padWith(str, annotation.length(), annotation.padding());
            } else if (field.isAnnotationPresent(TextField.class)) {
                TextField annotation = field.getAnnotation(TextField.class);
                int length = annotation.length();
                str = padWith(str, length, Padding.RIGHT_EMPTY);
            }

            if (field.isAnnotationPresent(Order.class)) {
                int order = (field.getAnnotation(Order.class)).value();
                tokens.add(order, str);
            } else {
                tokens.addFirst(str);
            }
        }

        final String msg = tokens.stream().reduce("", (x, y) -> x + y);
        return padWith(String.valueOf(msg.length()), 4, Padding.LEFT_EMPTY) + msg;
    }

    public static String padWith(final String txt, final int length, Padding pad) {

        String str = (null == txt) ? "" : txt;
        if (str.length() > length) {
            return StringUtils.truncate(str, length);
        } else {
            if (Padding.LEFT_ZERO == pad) {
                return StringUtils.leftPad(str, length, "0");
            } else if (Padding.LEFT_EMPTY == pad) {
                return StringUtils.leftPad(str, length);
            } else if (Padding.RIGHT_EMPTY == pad) {
                return StringUtils.rightPad(str, length);
            }
            return str;
        }
    }
}
