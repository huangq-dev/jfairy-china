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
package com.github.huangqdev.jfairychina.model;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Represents a generated Chinese person with identity information.
 * This is an immutable POJO containing all generated person data.
 */
public final class Person {

    /**
     * Full Chinese name (surname + given name)
     */
    private final String fullName;

    /**
     * Gender of the person (MALE or FEMALE)
     */
    private final String gender;

    /**
     * 18-digit Chinese Resident Identity Card number
     */
    private final String identityCardNumber;

    /**
     * Date of birth extracted from or used for identity card generation
     */
    private final LocalDate birthDate;

    /**
     * Province code (2-digit) from the identity card
     */
    private final String provinceCode;

    /**
     * Province name corresponding to the province code
     */
    private final String provinceName;

    private Person(String fullName, String gender, String identityCardNumber,
                   LocalDate birthDate, String provinceCode, String provinceName) {
        this.fullName = fullName;
        this.gender = gender;
        this.identityCardNumber = identityCardNumber;
        this.birthDate = birthDate;
        this.provinceCode = provinceCode;
        this.provinceName = provinceName;
    }

    /**
     * Gets the full Chinese name.
     *
     * @return the full name
     */
    public String getFullName() {
        return fullName;
    }

    /**
     * Gets the gender.
     *
     * @return "MALE" or "FEMALE"
     */
    public String getGender() {
        return gender;
    }

    /**
     * Gets the 18-digit Chinese Resident Identity Card number.
     *
     * @return the identity card number
     */
    public String getIdentityCardNumber() {
        return identityCardNumber;
    }

    /**
     * Gets the date of birth.
     *
     * @return the birth date
     */
    public LocalDate getBirthDate() {
        return birthDate;
    }

    /**
     * Gets the 2-digit province code.
     *
     * @return the province code
     */
    public String getProvinceCode() {
        return provinceCode;
    }

    /**
     * Gets the province name.
     *
     * @return the province name
     */
    public String getProvinceName() {
        return provinceName;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) return true;
        if (other == null || getClass() != other.getClass()) return false;
        Person person = (Person) other;
        return Objects.equals(fullName, person.fullName)
            && Objects.equals(gender, person.gender)
            && Objects.equals(identityCardNumber, person.identityCardNumber)
            && Objects.equals(birthDate, person.birthDate)
            && Objects.equals(provinceCode, person.provinceCode)
            && Objects.equals(provinceName, person.provinceName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullName, gender, identityCardNumber, birthDate, provinceCode, provinceName);
    }

    @Override
    public String toString() {
        return "Person{"
            + "fullName='" + fullName + '\''
            + ", gender='" + gender + '\''
            + ", identityCardNumber='" + identityCardNumber + '\''
            + ", birthDate=" + birthDate
            + ", provinceCode='" + provinceCode + '\''
            + ", provinceName='" + provinceName + '\''
            + '}';
    }

    /**
     * Creates a new Builder instance.
     *
     * @return a new Builder
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Builder for constructing Person instances.
     */
    public static final class Builder {

        private String fullName;
        private String gender;
        private String identityCardNumber;
        private LocalDate birthDate;
        private String provinceCode;
        private String provinceName;

        private Builder() {}

        /**
         * Sets the full name.
         *
         * @param fullName the full Chinese name
         * @return this builder
         */
        public Builder fullName(String fullName) {
            this.fullName = fullName;
            return this;
        }

        /**
         * Sets the gender.
         *
         * @param gender "MALE" or "FEMALE"
         * @return this builder
         */
        public Builder gender(String gender) {
            this.gender = gender;
            return this;
        }

        /**
         * Sets the identity card number.
         *
         * @param identityCardNumber the 18-digit ID number
         * @return this builder
         */
        public Builder identityCardNumber(String identityCardNumber) {
            this.identityCardNumber = identityCardNumber;
            return this;
        }

        /**
         * Sets the birth date.
         *
         * @param birthDate the date of birth
         * @return this builder
         */
        public Builder birthDate(LocalDate birthDate) {
            this.birthDate = birthDate;
            return this;
        }

        /**
         * Sets the province code.
         *
         * @param provinceCode the 2-digit province code
         * @return this builder
         */
        public Builder provinceCode(String provinceCode) {
            this.provinceCode = provinceCode;
            return this;
        }

        /**
         * Sets the province name.
         *
         * @param provinceName the province name
         * @return this builder
         */
        public Builder provinceName(String provinceName) {
            this.provinceName = provinceName;
            return this;
        }

        /**
         * Builds the Person instance.
         *
         * @return a new Person instance
         * @throws IllegalStateException if any required field is null
         */
        public Person build() {
            if (fullName == null) throw new IllegalStateException("fullName is required");
            if (gender == null) throw new IllegalStateException("gender is required");
            if (identityCardNumber == null) throw new IllegalStateException("identityCardNumber is required");
            if (birthDate == null) throw new IllegalStateException("birthDate is required");
            if (provinceCode == null) throw new IllegalStateException("provinceCode is required");
            if (provinceName == null) throw new IllegalStateException("provinceName is required");
            return new Person(fullName, gender, identityCardNumber, birthDate, provinceCode, provinceName);
        }
    }
}