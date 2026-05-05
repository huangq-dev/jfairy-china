package com.github.huangqdev.jfairychina.provider;

import com.github.huangqdev.jfairychina.repository.DataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

class PersonProviderTest {

    private PersonProvider provider;

    @BeforeEach
    void setUp() {
        provider = new PersonProvider(new Random(42), new DataRepository());
    }

    @Test
    void testGenerateFullName() {
        for (int i = 0; i < 100; i++) {
            String name = provider.generateFullName();
            assertNotNull(name);
            assertTrue(name.length() >= 2 && name.length() <= 5);
        }
    }

    @Test
    void testGenerateFullNameWithGender() {
        String maleName = provider.generateFullName(PersonProvider.Gender.MALE);
        assertNotNull(maleName);
        
        String femaleName = provider.generateFullName(PersonProvider.Gender.FEMALE);
        assertNotNull(femaleName);
    }

    @Test
    void testGenerateIdNumber() {
        for (int i = 0; i < 100; i++) {
            String id = provider.generateIdNumber();
            assertEquals(18, id.length());
            assertTrue(provider.isValidIdNumber(id));
        }
    }

    @Test
    void testGenerateIdNumberWithGender() {
        for (int i = 0; i < 50; i++) {
            String maleId = provider.generateIdNumber(PersonProvider.Gender.MALE);
            assertEquals(18, maleId.length());
            assertTrue(provider.isValidIdNumber(maleId));
            assertTrue((maleId.charAt(16) - '0') % 2 == 1);
            
            String femaleId = provider.generateIdNumber(PersonProvider.Gender.FEMALE);
            assertEquals(18, femaleId.length());
            assertTrue(provider.isValidIdNumber(femaleId));
            assertTrue((femaleId.charAt(16) - '0') % 2 == 0);
        }
    }

    @Test
    void testInvalidIdNumber() {
        assertFalse(provider.isValidIdNumber(null));
        assertFalse(provider.isValidIdNumber(""));
        assertFalse(provider.isValidIdNumber("12345678901234567"));
        assertFalse(provider.isValidIdNumber("1234567890123456789"));
    }
}