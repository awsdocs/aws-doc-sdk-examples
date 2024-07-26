package com.example.batch;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class Test {
    public static void main(String[] args) throws InterruptedException {
        int minutes = 2;
        int seconds = 0;
        for (int i = minutes * 60 + seconds; i >= 0; i--) {
            int displayMinutes = i / 60;
            int displaySeconds = i % 60;
            System.out.print(String.format("\r%02d:%02d", displayMinutes, displaySeconds));
            Thread.sleep(1000); // Wait for 1 second
        }
    }

    private static String formatCountdownString(Duration duration) {
        // Format the duration as a string in the format "mm:ss"
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("mm:ss");
        return formatter.format(LocalTime.MIN.plus(duration));
    }
}