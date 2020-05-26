package com.example.mission.payment.utils;

import java.text.DecimalFormat;
import java.util.concurrent.ThreadLocalRandom;

public class TidGenerator {

    private static final DecimalFormat formatter = new DecimalFormat("0");
    public static String generate() {

        double tid = ThreadLocalRandom.current().nextDouble(1_000_000_000_000_000_000D, 9_999_999_999_999_999_999D);
        return "T" + formatter.format(tid);
    }
}
