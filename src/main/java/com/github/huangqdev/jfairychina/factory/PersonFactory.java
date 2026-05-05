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
package com.github.huangqdev.jfairychina.factory;

import com.github.huangqdev.jfairychina.model.Person;
import com.github.huangqdev.jfairychina.repository.DataRepository;
import com.github.huangqdev.jfairychina.util.ChineseIDUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Factory class for generating realistic Chinese person mock data.
 * <p>
 * This factory combines data from {@link DataRepository} with the validation
 * logic in {@link ChineseIDUtils} to produce complete, standards-compliant
 * Chinese identity information.
 * <p>
 * Thread-safety: This class is thread-safe. Each instance uses its own
 * {@link Random} instance, and {@link DataRepository} is designed for
 * concurrent access.
 */
public final class PersonFactory {

    /**
     * Minimum age for generated persons
     */
    private static final int MIN_AGE = 18;

    /**
     * Maximum age for generated persons
     */
    private static final int MAX_AGE = 60;

    /**
     * Sequence number length in ID card (positions 15-17, 3 digits)
     */
    private static final int SEQUENCE_LENGTH = 3;

    /**
     * Maximum sequence number value (999)
     */
    private static final int MAX_SEQUENCE = 999;

    /**
     * Data repository for loading Chinese name and region data
     */
    private final DataRepository dataRepository;

    /**
     * Random number generator for deterministic or non-deterministic generation
     */
    private final Random random;

    /**
     * Cached list of province codes for efficient random selection
     */
    private volatile List<String> cachedProvinceCodes;

    /**
     * Constructs a PersonFactory with the specified data repository and random generator.
     *
     * @param dataRepository the data repository for loading names and region codes
     * @param random         the random number generator
     * @throws NullPointerException if dataRepository or random is null
     */
    public PersonFactory(DataRepository dataRepository, Random random) {
        this.dataRepository = Objects.requireNonNull(dataRepository, "dataRepository must not be null");
        this.random = Objects.requireNonNull(random, "random must not be null");
        this.cachedProvinceCodes = new java.util.ArrayList<>(dataRepository.loadProvinceCodes().keySet());
    }

    /**
     * Constructs a PersonFactory with the specified data repository using a default random generator.
     *
     * @param dataRepository the data repository for loading names and region codes
     * @throws NullPointerException if dataRepository is null
     */
    public PersonFactory(DataRepository dataRepository) {
        this(dataRepository, ThreadLocalRandom.current());
    }

    /**
     * Generates a random Chinese person with complete identity information.
     * <p>
     * The generated person includes:
     * <ul>
     *   <li>A realistic Chinese name (surname + given name)</li>
     *   <li>A gender-consistent given name</li>
     *   <li>A valid 18-digit identity card number with correct check digit</li>
     *   <li>A birth date between 18 and 60 years ago</li>
     *   <li>A province code from the 2026 administrative divisions</li>
     * </ul>
     *
     * @return a fully populated Person instance
     */
    public Person generateRandomPerson() {
        // Step 1: Determine gender first (used for name selection and ID sequence)
        String gender = generateRandomGender();

        // Step 2: Generate name based on gender
        String fullName = generateFullName(gender);

        // Step 3: Generate birth date (18-60 years old)
        LocalDate birthDate = generateRandomBirthDate();

        // Step 4: Select random province
        String provinceCode = generateRandomProvinceCode();
        String provinceName = dataRepository.loadProvinceCodes().get(provinceCode);

        // Step 5: Build identity card number
        String identityCardNumber = buildIdentityCardNumber(provinceCode, birthDate, gender);

        return Person.builder()
                .fullName(fullName)
                .gender(gender)
                .identityCardNumber(identityCardNumber)
                .birthDate(birthDate)
                .provinceCode(provinceCode)
                .provinceName(provinceName)
                .build();
    }

    /**
     * Generates a random Chinese person with the specified gender.
     *
     * @param gender the desired gender ("MALE" or "FEMALE")
     * @return a fully populated Person instance
     * @throws IllegalArgumentException if gender is not "MALE" or "FEMALE"
     */
    public Person generateRandomPerson(String gender) {
        if (!"MALE".equals(gender) && !"FEMALE".equals(gender)) {
            throw new IllegalArgumentException("Gender must be 'MALE' or 'FEMALE', but was: " + gender);
        }

        String fullName = generateFullName(gender);
        LocalDate birthDate = generateRandomBirthDate();
        String provinceCode = generateRandomProvinceCode();
        String provinceName = dataRepository.loadProvinceCodes().get(provinceCode);
        String identityCardNumber = buildIdentityCardNumber(provinceCode, birthDate, gender);

        return Person.builder()
                .fullName(fullName)
                .gender(gender)
                .identityCardNumber(identityCardNumber)
                .birthDate(birthDate)
                .provinceCode(provinceCode)
                .provinceName(provinceName)
                .build();
    }

    /**
     * Generates a random Chinese name (surname + given name).
     *
     * @return a full Chinese name
     */
    public String generateRandomName() {
        String gender = generateRandomGender();
        return generateFullName(gender);
    }

    /**
     * Generates a random Chinese name with the specified gender.
     *
     * @param gender the desired gender ("MALE" or "FEMALE")
     * @return a full Chinese name
     * @throws IllegalArgumentException if gender is not "MALE" or "FEMALE"
     */
    public String generateRandomName(String gender) {
        if (!"MALE".equals(gender) && !"FEMALE".equals(gender)) {
            throw new IllegalArgumentException("Gender must be 'MALE' or 'FEMALE', but was: " + gender);
        }
        return generateFullName(gender);
    }

    /**
     * Generates a valid 18-digit Chinese identity card number.
     *
     * @return a valid identity card number
     */
    public String generateRandomIdentityCardNumber() {
        String gender = generateRandomGender();
        LocalDate birthDate = generateRandomBirthDate();
        String provinceCode = generateRandomProvinceCode();
        return buildIdentityCardNumber(provinceCode, birthDate, gender);
    }

    /**
     * Builds a complete identity card number from components.
     *
     * @param provinceCode the 2-digit province code
     * @param birthDate    the date of birth
     * @param gender       the gender ("MALE" or "FEMALE")
     * @return a valid 18-digit identity card number
     */
    private String buildIdentityCardNumber(String provinceCode, LocalDate birthDate, String gender) {
        // Province code (2 digits) + city/district code (4 digits)
        String regionCode = provinceCode + generateRandomCityDistrictCode();

        // Birth date in yyyyMMdd format
        String birthDateString = birthDate.toString().replace("-", "");

        // Sequence number (3 digits) - odd for male, even for female
        String sequenceNumber = generateSequenceNumber(gender);

        // Combine first 17 digits
        String idBody = regionCode + birthDateString + sequenceNumber;

        // Calculate and append check digit
        char checkCode = ChineseIDUtils.calculateCheckCode(idBody);

        return idBody + checkCode;
    }

    /**
     * Generates a full Chinese name from surname and gender-specific given name.
     *
     * @param gender the gender for name selection
     * @return the full name
     */
    private String generateFullName(String gender) {
        List<String> surnames = dataRepository.loadSurnames();
        Map<String, List<String>> firstNames = dataRepository.loadFirstNames();

        String surname = surnames.get(random.nextInt(surnames.size()));
        List<String> givenNames = firstNames.get(gender.toLowerCase());

        if (givenNames == null || givenNames.isEmpty()) {
            throw new IllegalStateException("No given names found for gender: " + gender);
        }

        String givenName = givenNames.get(random.nextInt(givenNames.size()));
        return surname + givenName;
    }

    /**
     * Generates a random birth date between MIN_AGE and MAX_AGE years ago.
     *
     * @return a random birth date
     */
    private LocalDate generateRandomBirthDate() {
        LocalDate today = LocalDate.now();
        int minYear = today.getYear() - MAX_AGE;
        int maxYear = today.getYear() - MIN_AGE;

        int year = minYear + random.nextInt(maxYear - minYear + 1);
        int month = 1 + random.nextInt(12);

        // Handle month-day boundary (e.g., February 29)
        int maxDay = getDaysInMonth(year, month);
        int day = 1 + random.nextInt(maxDay);

        return LocalDate.of(year, month, day);
    }

    /**
     * Gets the number of days in a specific month of a given year.
     *
     * @param year  the year
     * @param month the month (1-12)
     * @return the number of days in that month
     */
    private int getDaysInMonth(int year, int month) {
        switch (month) {
            case 1: case 3: case 5: case 7: case 8: case 10: case 12:
                return 31;
            case 4: case 6: case 9: case 11:
                return 30;
            case 2:
                return isLeapYear(year) ? 29 : 28;
            default:
                throw new IllegalArgumentException("Invalid month: " + month);
        }
    }

    /**
     * Determines if a year is a leap year.
     *
     * @param year the year to check
     * @return true if leap year, false otherwise
     */
    private boolean isLeapYear(int year) {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
    }

    /**
     * Generates a random gender (MALE or FEMALE).
     *
     * @return "MALE" or "FEMALE"
     */
    private String generateRandomGender() {
        return random.nextBoolean() ? "MALE" : "FEMALE";
    }

    /**
     * Generates a random 4-digit city/district code.
     *
     * @return a 4-digit string (2-digit city + 2-digit district)
     */
    private String generateRandomCityDistrictCode() {
        String cityCode = String.format("%02d", random.nextInt(100));
        String districtCode = String.format("%02d", random.nextInt(100));
        return cityCode + districtCode;
    }

    /**
     * Generates a random province code from the cached province list.
     *
     * @return a valid province code string
     */
    private String generateRandomProvinceCode() {
        return cachedProvinceCodes.get(random.nextInt(cachedProvinceCodes.size()));
    }

    /**
     * Generates a 3-digit sequence number with correct parity for gender.
     * Odd numbers for male, even numbers for female.
     *
     * @param gender the gender
     * @return a 3-digit sequence number string
     */
    private String generateSequenceNumber(String gender) {
        int sequence;
        if ("MALE".equals(gender)) {
            // Odd number: 1, 3, 5, ..., 999
            sequence = 1 + 2 * random.nextInt(500);
        } else {
            // Even number: 0, 2, 4, ..., 998
            sequence = 2 * random.nextInt(500);
        }
        return String.format("%03d", sequence);
    }
}