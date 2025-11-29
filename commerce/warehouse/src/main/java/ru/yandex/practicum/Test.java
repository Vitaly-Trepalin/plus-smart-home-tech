package ru.yandex.practicum;


import java.security.SecureRandom;
import java.util.Random;
import java.util.Set;

public class Test {

    private static final String[] ADDRESSES = new String[] {"ADDRESS_1", "ADDRESS_2"};
    private static final String CURRENT_ADDRESS =
            ADDRESSES[Random.from(new SecureRandom()).nextInt(0, ADDRESSES.length)];

    public static void main(String[] args) {
        System.out.println(CURRENT_ADDRESS);

    }
}
