package de.uhd.ifi.se.moviemanager.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static de.uhd.ifi.se.moviemanager.util.PrimitiveUtils.fromObject;

import org.junit.jupiter.api.Test;

class PrimitiveUtilsTest {
    @Test
    void testFromObjectWithNonNullBoolean() {
        assertFalse(fromObject(Boolean.FALSE));
        assertTrue(fromObject(Boolean.TRUE));
    }

    @Test
    void testFromObjectWithNonNullInteger() {
        assertEquals(-256, fromObject(-256));
        assertEquals(0, fromObject(0));
        assertEquals(256, fromObject(256));
    }
}
