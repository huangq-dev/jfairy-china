/*
 * Copyright (c) 2026 Qiang (Quentin) Huang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.huangqdev.jfairychina.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

/**
 * Utility class for validating and extracting information from Chinese Resident Identity Card numbers.
 * Implements the GB 11643-1999 standard and ISO 7064:1983.MOD 11-2 checksum algorithm.
 */
public final class ChineseIDUtils {

    /**
     * Total length of the second-generation Chinese Resident Identity Card
     */
    public static final int ID_LENGTH = 18;

    /**
     * Length of the ID body without the check digit
     */
    public static final int BODY_LENGTH = 17;

    /**
     * Start position of the province code
     */
    public static final int PROVINCE_START = 0;

    /**
     * Length of the province code
     */
    public static final int PROVINCE_LENGTH = 2;

    /**
     * Start position of the birth date
     */
    public static final int BIRTH_DATE_START = 6;

    /**
     * Length of the birth date
     */
    public static final int BIRTH_DATE_LENGTH = 8;

    /**
     * Position of the gender digit (second to last)
     */
    public static final int GENDER_POSITION = 16;

    /**
     * Date formatter for birth date (yyyyMMdd)
     */
    private static final DateTimeFormatter BIRTH_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * Weight factors array based on ISO 7064:1983.MOD 11-2 algorithm
     */
    private static final int[] WEIGHTS = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};

    /**
     * Check code array for validation
     */
    private static final char[] CHECK_CODES = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};

    /**
     * Minimum allowed birth year
     */
    private static final int MIN_BIRTH_YEAR = 1900;

    /**
     * Maximum allowed birth year (dynamically calculated from current year)
     */
    private static final int MAX_BIRTH_YEAR = LocalDate.now().getYear();

    private ChineseIDUtils() {}

    /**
     * Calculates the check digit for a Chinese Resident Identity Card number.
     * Implements the ISO 7064:1983.MOD 11-2 algorithm as specified in GB 11643-1999.
     *
     * @param idWithoutCheckCode The 17-digit ID body without the check digit
     * @return The calculated check digit character
     * @throws IllegalArgumentException If the input is null, not exactly 17 characters, or contains non-digit characters
     */
    public static char calculateCheckCode(String idWithoutCheckCode) {
        // Null check
        if (idWithoutCheckCode == null) {
            throw new IllegalArgumentException("ID body cannot be null");
        }

        // Length validation
        if (idWithoutCheckCode.length() != BODY_LENGTH) {
            throw new IllegalArgumentException(
                String.format("ID body must be exactly %d characters, but got %d", BODY_LENGTH, idWithoutCheckCode.length()));
        }

        // Digit validation
        for (int i = 0; i < BODY_LENGTH; i++) {
            char c = idWithoutCheckCode.charAt(i);
            if (!Character.isDigit(c)) {
                throw new IllegalArgumentException(
                    String.format("ID body contains non-digit character '%c' at position %d", c, i));
            }
        }

        // Calculate weighted sum
        int sum = 0;
        for (int i = 0; i < BODY_LENGTH; i++) {
            int digit = idWithoutCheckCode.charAt(i) - '0';
            sum += digit * WEIGHTS[i];
        }

        // Calculate check digit
        int remainder = sum % 11;
        return CHECK_CODES[remainder];
    }

    /**
     * Validates a Chinese Resident Identity Card number.
     * Checks format, birth date validity, and check digit.
     *
     * @param idNumber The 18-digit ID number to validate
     * @return true if the ID number is valid, false otherwise
     */
    public static boolean isValid(String idNumber) {
        // Null check
        if (idNumber == null) {
            return false;
        }

        // Length check
        if (idNumber.length() != ID_LENGTH) {
            return false;
        }

        // Convert to uppercase to handle 'X' case-insensitively
        String upperId = idNumber.toUpperCase();

        // First 17 characters must be digits
        for (int i = 0; i < BODY_LENGTH; i++) {
            if (!Character.isDigit(upperId.charAt(i))) {
                return false;
            }
        }

        // Last character must be digit or 'X'
        char lastChar = upperId.charAt(ID_LENGTH - 1);
        if (!Character.isDigit(lastChar) && lastChar != 'X') {
            return false;
        }

        // Validate birth date
        String birthDateStr = upperId.substring(BIRTH_DATE_START, BIRTH_DATE_START + BIRTH_DATE_LENGTH);
        if (!isValidBirthDate(birthDateStr)) {
            return false;
        }

        // Validate check digit
        String prefix = upperId.substring(0, BODY_LENGTH);
        char expectedCheckCode = calculateCheckCode(prefix);

        return upperId.charAt(ID_LENGTH - 1) == expectedCheckCode;
    }

    /**
     * Validates the birth date portion of an ID number.
     * Ensures the date is within valid range (1900-2026) and not in the future.
     *
     * @param birthDateStr The 8-digit birth date string (yyyyMMdd)
     * @return true if the birth date is valid, false otherwise
     */
    private static boolean isValidBirthDate(String birthDateStr) {
        if (birthDateStr == null || birthDateStr.length() != BIRTH_DATE_LENGTH) {
            return false;
        }

        try {
            LocalDate birthDate = LocalDate.parse(birthDateStr, BIRTH_DATE_FORMATTER);

            // Check year range
            int year = birthDate.getYear();
            if (year < MIN_BIRTH_YEAR || year > MAX_BIRTH_YEAR) {
                return false;
            }

            // Check if birth date is not in the future
            LocalDate today = LocalDate.now();
            return !birthDate.isAfter(today);

        } catch (DateTimeParseException e) {
            return false;
        }
    }

    /**
     * Extracts the province code from an ID number.
     *
     * @param idNumber The ID number to extract from
     * @return The 2-digit province code, or null if invalid
     */
    public static String extractProvince(String idNumber) {
        if (idNumber == null || idNumber.length() < PROVINCE_START + PROVINCE_LENGTH) {
            return null;
        }
        return idNumber.substring(PROVINCE_START, PROVINCE_START + PROVINCE_LENGTH);
    }

    /**
     * Extracts the birth date from an ID number.
     *
     * @param idNumber The ID number to extract from
     * @return The 8-digit birth date string (yyyyMMdd), or null if invalid
     */
    public static String extractBirthDate(String idNumber) {
        if (idNumber == null || idNumber.length() < BIRTH_DATE_START + BIRTH_DATE_LENGTH) {
            return null;
        }
        return idNumber.substring(BIRTH_DATE_START, BIRTH_DATE_START + BIRTH_DATE_LENGTH);
    }

    /**
     * Extracts the gender from an ID number.
     * The 17th digit (index 16) determines gender: odd for male, even for female.
     *
     * @param idNumber The ID number to extract from
     * @return "MALE" for odd digit, "FEMALE" for even digit, or null if invalid
     */
    public static String extractGender(String idNumber) {
        if (idNumber == null || idNumber.length() <= GENDER_POSITION) {
            return null;
        }

        char genderChar = idNumber.charAt(GENDER_POSITION);
        if (!Character.isDigit(genderChar)) {
            return null;
        }

        int genderDigit = genderChar - '0';
        return genderDigit % 2 == 0 ? "FEMALE" : "MALE";
    }
}