package com.github.huangqdev.jfairychina;

import com.github.huangqdev.jfairychina.provider.PersonProvider;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FairyTest {

    @Test
    void testCreateDefault() {
        Fairy fairy = Fairy.create();
        assertNotNull(fairy);
        assertNotNull(fairy.person());
        assertNotNull(fairy.getRandom());
    }

    @Test
    void testCreateWithSeed() {
        Fairy fairy1 = Fairy.create(42);
        Fairy fairy2 = Fairy.create(42);
        
        String id1 = fairy1.person().generateIdNumber();
        String id2 = fairy2.person().generateIdNumber();
        
        assertEquals(id1, id2);
    }

    @Test
    void testPersonProvider() {
        Fairy fairy = Fairy.create(123);
        
        String fullName = fairy.person().generateFullName();
        assertNotNull(fullName);
        assertTrue(fullName.length() >= 2);
        
        String idNumber = fairy.person().generateIdNumber();
        assertEquals(18, idNumber.length());
        assertTrue(fairy.person().isValidIdNumber(idNumber));
    }
}