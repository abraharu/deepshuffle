package org.example.deepshuffle.spotify.discovery.seed;

import org.springframework.stereotype.Component;

import java.util.concurrent.ThreadLocalRandom;

@Component
public class RandomQueryGenerator {

    private static final String LATIN = "abcdefghijklmnopqrstuvwxyz";
    private static final String CYRILLIC = "абвгдежзийклмнопрстуфхцчшщьыэюя";
    private static final String DIGITS = "0123456789";
    private static final String MIXED = LATIN + CYRILLIC + DIGITS;

    public String randomQuery() {
        String alphabet = randomAlphabet();
        int length = randomLength();

        StringBuilder query = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            query.append(alphabet.charAt(ThreadLocalRandom.current().nextInt(alphabet.length())));
        }

        return query.toString();
    }

    private String randomAlphabet() {
        int roll = ThreadLocalRandom.current().nextInt(100);
        if (roll < 45) {
            return LATIN;
        }
        if (roll < 70) {
            return CYRILLIC;
        }
        if (roll < 85) {
            return DIGITS;
        }
        return MIXED;
    }

    private int randomLength() {
        int roll = ThreadLocalRandom.current().nextInt(100);
        if (roll < 40) {
            return 1;
        }
        if (roll < 75) {
            return 2;
        }
        if (roll < 95) {
            return 3;
        }
        return 4;
    }
}
