package com.linkshortener.utilities;

import java.util.Random;

public class RandomStringGenerator {
    public static void main(String[] args) {
        int length = 6; // Length of the random string
        String randomString = generateRandomString(length);
        System.out.println("Random String: " + randomString);
    }

    public static String generateRandomString(int length) {
        String characters = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int randomIndex = random.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            stringBuilder.append(randomChar);
        }

        return stringBuilder.toString();
    }
}
