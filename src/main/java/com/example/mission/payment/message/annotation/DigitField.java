package com.example.mission.payment.message.annotation;

import com.example.mission.payment.message.type.Padding;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DigitField {
    int length();
    Padding padding() default Padding.LEFT_EMPTY;
}
