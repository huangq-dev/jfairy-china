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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("ChineseIDUtils Tests")
class ChineseIDUtilsTest {

    // Valid ID numbers for testing (calculated with ISO 7064:1983.MOD 11-2)
    // Format: Province-City-District-BirthDate-Sequence-Gender-CheckDigit
    private static final String VALID_BEIJING_MALE_1990 = "110101199001010015";
    private static final String VALID_SHANGHAI_FEMALE_1985 = "310101198502010024";
    private static final String VALID_GUANGDONG_MALE_2000 = "440301200001010034";
    private static final String VALID_HUBEI_MALE_1900 = "420101190001010017";
    private static final String VALID_BEIJING_MALE_LEAP_2024 = "110101202402290104";
    private static final String VALID_TIANJIN_FEMALE_1900 = "120101190001010024";

    @Nested
    @DisplayName("isValid() - Happy Path")
    class IsValidHappyPath {

        @ParameterizedTest
        @ValueSource(strings = {
            VALID_BEIJING_MALE_1990,
            VALID_SHANGHAI_FEMALE_1985,
            VALID_GUANGDONG_MALE_2000,
            VALID_HUBEI_MALE_1900,
            VALID_BEIJING_MALE_LEAP_2024,
            VALID_TIANJIN_FEMALE_1900
        })
        @DisplayName("should return true for valid ID numbers from various provinces")
        void shouldReturnTrueForValidIdNumbers(String validId) {
            assertThat(ChineseIDUtils.isValid(validId)).isTrue();
        }

        @Test
        @DisplayName("should handle lowercase 'x' in check digit")
        void shouldHandleLowercaseX() {
            String idWithLowercaseX = "310101198502010024".toLowerCase();
            assertThat(ChineseIDUtils.isValid(idWithLowercaseX)).isTrue();
        }
    }

    @Nested
    @DisplayName("isValid() - Boundary Conditions")
    class IsValidBoundaryConditions {

        @Test
        @DisplayName("should accept birth date at minimum boundary (1900-01-01)")
        void shouldAcceptMinimumBirthDate() {
            assertThat(ChineseIDUtils.isValid(VALID_HUBEI_MALE_1900)).isTrue();
        }

        @Test
        @DisplayName("should accept leap year date (2024-02-29)")
        void shouldAcceptLeapYearDate() {
            assertThat(ChineseIDUtils.isValid(VALID_BEIJING_MALE_LEAP_2024)).isTrue();
        }

        @Test
        @DisplayName("should accept birth date at current year boundary")
        void shouldAcceptCurrentYearBirthDate() {
            String currentYearId = "110101202601010013";
            assertThat(ChineseIDUtils.isValid(currentYearId)).isTrue();
        }
    }

    @Nested
    @DisplayName("isValid() - Negative Cases")
    class IsValidNegativeCases {

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("should return false for null or empty input")
        void shouldReturnFalseForNullOrEmpty(String invalidId) {
            assertThat(ChineseIDUtils.isValid(invalidId)).isFalse();
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "11010119900101001",   // 17 digits (too short)
            "1101011990010100156", // 19 digits (too long)
            "12345",               // 5 digits
            ""                     // empty string
        })
        @DisplayName("should return false for invalid length")
        void shouldReturnFalseForInvalidLength(String invalidId) {
            assertThat(ChineseIDUtils.isValid(invalidId)).isFalse();
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "11010119900101001A",  // Non-digit in check position (not X)
            "110101199A01010015",  // Non-digit in body
            "1101011990010100!5",  // Special character
            "1101011990010100 5",  // Space character
        })
        @DisplayName("should return false for invalid characters")
        void shouldReturnFalseForInvalidCharacters(String invalidId) {
            assertThat(ChineseIDUtils.isValid(invalidId)).isFalse();
        }

        @Test
        @DisplayName("should return false for future birth date")
        void shouldReturnFalseForFutureBirthDate() {
            // Birth date: 2027-01-01 (future)
            String futureId = "110101202701010014";
            assertThat(ChineseIDUtils.isValid(futureId)).isFalse();
        }

        @Test
        @DisplayName("should return false for invalid month (13)")
        void shouldReturnFalseForInvalidMonth() {
            // Birth date: 1990-13-01 (invalid month)
            String invalidMonthId = "110101199013010015";
            assertThat(ChineseIDUtils.isValid(invalidMonthId)).isFalse();
        }

        @Test
        @DisplayName("should return false for invalid day (32)")
        void shouldReturnFalseForInvalidDay() {
            // Birth date: 1990-01-32 (invalid day)
            String invalidDayId = "110101199001320015";
            assertThat(ChineseIDUtils.isValid(invalidDayId)).isFalse();
        }

        @Test
        @DisplayName("should return false for non-leap year Feb 29")
        void shouldReturnFalseForNonLeapYearFeb29() {
            // Birth date: 2023-02-29 (2023 is not a leap year)
            String nonLeapFeb29 = "110101202302290015";
            assertThat(ChineseIDUtils.isValid(nonLeapFeb29)).isFalse();
        }

        @Test
        @DisplayName("should return false for birth year before 1900")
        void shouldReturnFalseForBirthYearBefore1900() {
            // Birth date: 1899-01-01
            String oldId = "110101189901010015";
            assertThat(ChineseIDUtils.isValid(oldId)).isFalse();
        }

        @Test
        @DisplayName("should return false for correct body but wrong check digit")
        void shouldReturnFalseForWrongCheckDigit() {
            // Valid body: 11010119900101001, correct check digit is 5
            String wrongCheckDigit = "110101199001010012";
            assertThat(ChineseIDUtils.isValid(wrongCheckDigit)).isFalse();
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "110101199001010011",  // Wrong check digit: 1
            "110101199001010012",  // Wrong check digit: 2
            "110101199001010013",  // Wrong check digit: 3
            "110101199001010014",  // Wrong check digit: 4
            "110101199001010016",  // Wrong check digit: 6
            "110101199001010017",  // Wrong check digit: 7
            "110101199001010018",  // Wrong check digit: 8
            "110101199001010019",  // Wrong check digit: 9
            "110101199001010010",  // Wrong check digit: 0
            "11010119900101001X",  // Wrong check digit: X
        })
        @DisplayName("should return false for all incorrect check digits")
        void shouldReturnFalseForAllWrongCheckDigits(String wrongId) {
            assertThat(ChineseIDUtils.isValid(wrongId)).isFalse();
        }
    }

    @Nested
    @DisplayName("calculateCheckCode() - Tests")
    class CalculateCheckCodeTests {

        @ParameterizedTest
        @ValueSource(strings = {
            "11010119900101001",  // Beijing male 1990
            "31010119850201002",  // Shanghai female 1985
            "44030120000101003",  // Guangdong male 2000
            "42010119000101001",  // Hubei male 1900
            "11010120240229010",  // Beijing male leap 2024
        })
        @DisplayName("should calculate correct check digit for valid 17-digit body")
        void shouldCalculateCorrectCheckDigit(String idBody) {
            char checkCode = ChineseIDUtils.calculateCheckCode(idBody);

            // Verify by constructing full ID and validating
            String fullId = idBody + checkCode;
            assertThat(ChineseIDUtils.isValid(fullId)).isTrue();
        }

        @Test
        @DisplayName("should throw exception for null input")
        void shouldThrowExceptionForNullInput() {
            assertThatThrownBy(() -> ChineseIDUtils.calculateCheckCode(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("null");
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "1101011990010100",   // 16 digits (too short)
            "110101199001010012", // 18 digits (too long)
            "123",                // 3 digits
        })
        @DisplayName("should throw exception for invalid length")
        void shouldThrowExceptionForInvalidLength(String invalidBody) {
            assertThatThrownBy(() -> ChineseIDUtils.calculateCheckCode(invalidBody))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("exactly 17 characters");
        }

        @ParameterizedTest
        @ValueSource(strings = {
            "1101011990010100A",  // Contains letter
            "1101011990010100!",  // Contains special character
            "1101011990010100 ",  // Contains space
        })
        @DisplayName("should throw exception for non-digit characters")
        void shouldThrowExceptionForNonDigitCharacters(String invalidBody) {
            assertThatThrownBy(() -> ChineseIDUtils.calculateCheckCode(invalidBody))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("non-digit character");
        }
    }

    @Nested
    @DisplayName("extractProvince() - Tests")
    class ExtractProvinceTests {

        @Test
        @DisplayName("should extract province code from valid ID")
        void shouldExtractProvinceFromValidId() {
            assertThat(ChineseIDUtils.extractProvince(VALID_BEIJING_MALE_1990)).isEqualTo("11");
            assertThat(ChineseIDUtils.extractProvince(VALID_SHANGHAI_FEMALE_1985)).isEqualTo("31");
            assertThat(ChineseIDUtils.extractProvince(VALID_GUANGDONG_MALE_2000)).isEqualTo("44");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("should return null for null or empty input")
        void shouldReturnNullForNullOrEmpty(String invalidId) {
            assertThat(ChineseIDUtils.extractProvince(invalidId)).isNull();
        }

        @Test
        @DisplayName("should return null for ID shorter than 2 characters")
        void shouldReturnNullForShortId() {
            assertThat(ChineseIDUtils.extractProvince("1")).isNull();
        }
    }

    @Nested
    @DisplayName("extractBirthDate() - Tests")
    class ExtractBirthDateTests {

        @Test
        @DisplayName("should extract birth date from valid ID")
        void shouldExtractBirthDateFromValidId() {
            assertThat(ChineseIDUtils.extractBirthDate(VALID_BEIJING_MALE_1990)).isEqualTo("19900101");
            assertThat(ChineseIDUtils.extractBirthDate(VALID_SHANGHAI_FEMALE_1985)).isEqualTo("19850201");
            assertThat(ChineseIDUtils.extractBirthDate(VALID_BEIJING_MALE_LEAP_2024)).isEqualTo("20240229");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("should return null for null or empty input")
        void shouldReturnNullForNullOrEmpty(String invalidId) {
            assertThat(ChineseIDUtils.extractBirthDate(invalidId)).isNull();
        }

        @Test
        @DisplayName("should return null for ID shorter than 14 characters")
        void shouldReturnNullForShortId() {
            assertThat(ChineseIDUtils.extractBirthDate("110101")).isNull();
        }
    }

    @Nested
    @DisplayName("extractGender() - Tests")
    class ExtractGenderTests {

        @Test
        @DisplayName("should return MALE for odd gender digit")
        void shouldReturnMaleForOddDigit() {
            assertThat(ChineseIDUtils.extractGender(VALID_BEIJING_MALE_1990)).isEqualTo("MALE");
            assertThat(ChineseIDUtils.extractGender(VALID_GUANGDONG_MALE_2000)).isEqualTo("MALE");
        }

        @Test
        @DisplayName("should return FEMALE for even gender digit")
        void shouldReturnFemaleForEvenDigit() {
            assertThat(ChineseIDUtils.extractGender(VALID_SHANGHAI_FEMALE_1985)).isEqualTo("FEMALE");
            assertThat(ChineseIDUtils.extractGender(VALID_TIANJIN_FEMALE_1900)).isEqualTo("FEMALE");
        }

        @ParameterizedTest
        @NullAndEmptySource
        @DisplayName("should return null for null or empty input")
        void shouldReturnNullForNullOrEmpty(String invalidId) {
            assertThat(ChineseIDUtils.extractGender(invalidId)).isNull();
        }

        @Test
        @DisplayName("should return null for ID shorter than 17 characters")
        void shouldReturnNullForShortId() {
            assertThat(ChineseIDUtils.extractGender("110101199001010")).isNull();
        }
    }

    @Nested
    @DisplayName("Constants - Verification")
    class ConstantsVerification {

        @Test
        @DisplayName("should have correct ID_LENGTH constant")
        void shouldHaveCorrectIdLength() {
            assertThat(ChineseIDUtils.ID_LENGTH).isEqualTo(18);
        }

        @Test
        @DisplayName("should have correct BODY_LENGTH constant")
        void shouldHaveCorrectBodyLength() {
            assertThat(ChineseIDUtils.BODY_LENGTH).isEqualTo(17);
        }

        @Test
        @DisplayName("should have correct PROVINCE_LENGTH constant")
        void shouldHaveCorrectProvinceLength() {
            assertThat(ChineseIDUtils.PROVINCE_LENGTH).isEqualTo(2);
        }

        @Test
        @DisplayName("should have correct BIRTH_DATE_LENGTH constant")
        void shouldHaveCorrectBirthDateLength() {
            assertThat(ChineseIDUtils.BIRTH_DATE_LENGTH).isEqualTo(8);
        }

        @Test
        @DisplayName("should have correct GENDER_POSITION constant")
        void shouldHaveCorrectGenderPosition() {
            assertThat(ChineseIDUtils.GENDER_POSITION).isEqualTo(16);
        }
    }
}