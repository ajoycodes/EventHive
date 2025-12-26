package com.example.eventhive.utils;

import java.util.Random;

/**
 * Utility class for generating unique ticket codes.
 * Format: EVT-{EVENT_ID}-{TIMESTAMP}-{RANDOM_4_DIGITS}
 */
public class TicketCodeGenerator {

    private static final Random random = new Random();

    /**
     * Generates a unique ticket code for an event.
     * 
     * @param eventId The ID of the event
     * @return Unique ticket code in format:
     *         EVT-{EVENT_ID}-{TIMESTAMP}-{RANDOM_4_DIGITS}
     */
    public static String generateTicketCode(int eventId) {
        long timestamp = System.currentTimeMillis();
        int randomDigits = 1000 + random.nextInt(9000); // 4-digit random number (1000-9999)

        return String.format("EVT-%d-%d-%04d", eventId, timestamp, randomDigits);
    }

    /**
     * Validates ticket code format.
     * 
     * @param ticketCode The ticket code to validate
     * @return true if format is valid, false otherwise
     */
    public static boolean isValidFormat(String ticketCode) {
        if (ticketCode == null || ticketCode.isEmpty()) {
            return false;
        }

        // Check if matches pattern: EVT-{number}-{number}-{4digits}
        return ticketCode.matches("^EVT-\\d+-\\d+-\\d{4}$");
    }
}
