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

import java.util.List;
import java.util.Map;
import java.util.Random;

public class PersonProvider {

    private final Random random;
    private final DataRepository dataRepository;
    private final List<String> surnames;
    private final List<String> maleFirstNames;
    private final List<String> femaleFirstNames;
    private final List<String> provinceCodes;

    public PersonProvider(Random random, DataRepository dataRepository) {
        this.random = random;
        this.dataRepository = dataRepository;
        this.surnames = dataRepository.loadSurnames();
        
        Map<String, List<String>> firstNames = dataRepository.loadFirstNames();
        this.maleFirstNames = firstNames.get("male");
        this.femaleFirstNames = firstNames.get("female");
        
        this.provinceCodes = dataRepository.getProvinceCodeList();
    }

    public String generateFirstName(Gender gender) {
        List<String> names = gender == Gender.MALE ? maleFirstNames : femaleFirstNames;
        return names.get(random.nextInt(names.size()));
    }

    public String generateFirstName() {
        return generateFirstName(random.nextBoolean() ? Gender.MALE : Gender.FEMALE);
    }

    public String generateSurname() {
        return surnames.get(random.nextInt(surnames.size()));
    }

    public String generateFullName() {
        return generateSurname() + generateFirstName();
    }

    public String generateFullName(Gender gender) {
        return generateSurname() + generateFirstName(gender);
    }

    public String generateIdNumber() {
        String provinceCode = provinceCodes.get(random.nextInt(provinceCodes.size()));
        String cityCode = String.format("%02d", random.nextInt(100));
        String districtCode = String.format("%02d", random.nextInt(100));
        String birthDate = generateBirthDate();
        String sequenceCode = String.format("%03d", random.nextInt(1000));
        
        String prefix = provinceCode + cityCode + districtCode + birthDate + sequenceCode;
        char checkCode = ChineseIDUtils.calculateCheckCode(prefix);
        
        return prefix + checkCode;
    }

    public String generateIdNumber(Gender gender) {
        String provinceCode = provinceCodes.get(random.nextInt(provinceCodes.size()));
        String cityCode = String.format("%02d", random.nextInt(100));
        String districtCode = String.format("%02d", random.nextInt(100));
        String birthDate = generateBirthDate();
        
        int seq = random.nextInt(500);
        int genderDigit = gender == Gender.MALE ? seq * 2 + 1 : seq * 2;
        String sequenceCode = String.format("%03d", genderDigit);
        
        String prefix = provinceCode + cityCode + districtCode + birthDate + sequenceCode;
        char checkCode = ChineseIDUtils.calculateCheckCode(prefix);
        
        return prefix + checkCode;
    }

    private String generateBirthDate() {
        int year = 1950 + random.nextInt(70);
        int month = 1 + random.nextInt(12);
        int day = 1 + random.nextInt(28);
        return String.format("%04d%02d%02d", year, month, day);
    }

    public boolean isValidIdNumber(String idNumber) {
        return ChineseIDUtils.isValid(idNumber);
    }

    public enum Gender {
        MALE, FEMALE
    }
}