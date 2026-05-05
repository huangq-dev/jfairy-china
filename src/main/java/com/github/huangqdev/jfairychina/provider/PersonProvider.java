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
package com.github.huangqdev.jfairychina.provider;

import com.github.huangqdev.jfairychina.repository.DataRepository;
import com.github.huangqdev.jfairychina.util.ChineseIDUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Provider for generating Chinese person-related mock data.
 * <p>
 * This class provides methods to generate Chinese names, identity card numbers,
 * and validate ID number formats. All generated data follows Chinese national
 * standards (GB/T 2260 for administrative divisions, GB 11643-1999 for ID cards).
 * <p>
 * Thread-safety: This class is thread-safe when constructed with ThreadLocalRandom.
 * The internal data lists are immutable after initialization.
 */
public class PersonProvider {

    /**
     * Minimum age for generated persons
     */
    private static final int MIN_AGE = 18;

    /**
     * Maximum age for generated persons
     */
    private static final int MAX_AGE = 60;

    /**
     * Random number generator for data generation
     */
    private final Random random;

    /**
     * Data repository for loading Chinese name and region data
     */
    private final DataRepository dataRepository;

    /**
     * Immutable list of Chinese surnames
     */
    private final List<String> surnames;

    /**
     * Immutable list of male given names
     */
    private final List<String> maleFirstNames;

    /**
     * Immutable list of female given names
     */
    private final List<String> femaleFirstNames;

    /**
     * Immutable list of province codes
     */
    private final List<String> provinceCodes;

    /**
     * Immutable list of city/district codes for valid region generation
     */
    private final List<String> cityDistrictCodes;

    /**
     * Constructs a PersonProvider with the specified random generator and data repository.
     *
     * @param random         the random number generator
     * @param dataRepository the data repository for loading names and region codes
     * @throws NullPointerException if random or dataRepository is null
     */
    public PersonProvider(Random random, DataRepository dataRepository) {
        this.random = random;
        this.dataRepository = dataRepository;
        this.surnames = dataRepository.loadSurnames();

        Map<String, List<String>> firstNames = dataRepository.loadFirstNames();
        this.maleFirstNames = firstNames.get("male");
        this.femaleFirstNames = firstNames.get("female");

        this.provinceCodes = dataRepository.getProvinceCodeList();
        this.cityDistrictCodes = dataRepository.loadCityDistrictCodes();
    }

    /**
     * Generates a random Chinese given name for the specified gender.
     *
     * @param gender the desired gender (MALE or FEMALE)
     * @return a Chinese given name
     */
    public String generateFirstName(Gender gender) {
        List<String> names = gender == Gender.MALE ? maleFirstNames : femaleFirstNames;
        return names.get(random.nextInt(names.size()));
    }

    /**
     * Generates a random Chinese given name with random gender.
     *
     * @return a Chinese given name
     */
    public String generateFirstName() {
        return generateFirstName(random.nextBoolean() ? Gender.MALE : Gender.FEMALE);
    }

    /**
     * Generates a random Chinese surname.
     *
     * @return a Chinese surname
     */
    public String generateSurname() {
        return surnames.get(random.nextInt(surnames.size()));
    }

    /**
     * Generates a random Chinese full name with random gender.
     *
     * @return a Chinese full name (surname + given name)
     */
    public String generateFullName() {
        return generateSurname() + generateFirstName();
    }

    /**
     * Generates a random Chinese full name for the specified gender.
     *
     * @param gender the desired gender (MALE or FEMALE)
     * @return a Chinese full name (surname + given name)
     */
    public String generateFullName(Gender gender) {
        return generateSurname() + generateFirstName(gender);
    }

    /**
     * Generates a valid 18-digit Chinese Resident Identity Card number with random gender.
     * <p>
     * The generated ID number follows GB 11643-1999 standard with:
     * <ul>
     *   <li>Valid province code from GB/T 2260</li>
     *   <li>Valid city/district code from loaded data</li>
     *   <li>Realistic birth date (18-60 years old)</li>
     *   <li>Correct check digit using ISO 7064:1983.MOD 11-2</li>
     * </ul>
     *
     * @return a valid 18-digit Chinese ID number
     */
    public String generateIdNumber() {
        return generateIdNumber(random.nextBoolean() ? Gender.MALE : Gender.FEMALE);
    }

    /**
     * Generates a valid 18-digit Chinese Resident Identity Card number for the specified gender.
     *
     * @param gender the desired gender (MALE or FEMALE)
     * @return a valid 18-digit Chinese ID number with correct gender digit
     */
    public String generateIdNumber(Gender gender) {
        String provinceCode = provinceCodes.get(random.nextInt(provinceCodes.size()));
        String cityDistrictCode = cityDistrictCodes.get(random.nextInt(cityDistrictCodes.size()));
        String birthDate = generateBirthDate();
        String sequenceCode = generateSequenceCode(gender);

        String prefix = provinceCode + cityDistrictCode + birthDate + sequenceCode;
        char checkCode = ChineseIDUtils.calculateCheckCode(prefix);

        return prefix + checkCode;
    }

    /**
     * Generates a random birth date string in yyyyMMdd format.
     * The generated date ensures the person is between MIN_AGE and MAX_AGE years old.
     *
     * @return birth date string in yyyyMMdd format
     */
    private String generateBirthDate() {
        LocalDate today = LocalDate.now();
        int minYear = today.getYear() - MAX_AGE;
        int maxYear = today.getYear() - MIN_AGE;

        int year = minYear + random.nextInt(maxYear - minYear + 1);
        int month = 1 + random.nextInt(12);

        int maxDay = getDaysInMonth(year, month);
        int day = 1 + random.nextInt(maxDay);

        LocalDate birthDate = LocalDate.of(year, month, day);
        return birthDate.toString().replace("-", "");
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
     * Generates a 3-digit sequence code with correct parity for gender.
     * Odd numbers for male, even numbers for female (positions 15-17 of ID card).
     *
     * @param gender the gender
     * @return a 3-digit sequence code string
     */
    private String generateSequenceCode(Gender gender) {
        int sequence;
        if (gender == Gender.MALE) {
            sequence = 1 + 2 * random.nextInt(500);
        } else {
            sequence = 2 * random.nextInt(500);
        }
        return String.format("%03d", sequence);
    }

    /**
     * Validates a Chinese Resident Identity Card number.
     *
     * @param idNumber the ID number to validate
     * @return true if the ID number is valid, false otherwise
     */
    public boolean isValidIdNumber(String idNumber) {
        return ChineseIDUtils.isValid(idNumber);
    }

    /**
     * Gender enumeration for person data generation.
     */
    public enum Gender {
        MALE, FEMALE
    }
}