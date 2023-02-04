package de.uhd.ifi.se.moviemanager.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class RateableTest {

    @Test
    @DisplayName("\u2606\u2606\u2606\u2606\u2606 - " + "\u2605\u2605\u2605" + "\u2605\u2605")
    void testAllPossibilitiesForTextRatingBar() {
        double[] ratings = {0.0, 0.5, 1.0, 1.5, 2.0, 2.5, 3.0, 3.5, 4.0, 4.5,
                5.0};
        String[] expectedStrings = {"\u2606\u2606\u2606\u2606\u2606", "\u272F"
                + "\u2606\u2606\u2606\u2606", "\u2605\u2606\u2606\u2606\u2606"
                , "\u2605\u272F\u2606\u2606\u2606", "\u2605\u2605\u2606\u2606"
                + "\u2606", "\u2605\u2605\u272F\u2606\u2606",
                "\u2605\u2605" + "\u2605\u2606\u2606",
                "\u2605\u2605\u2605" + "\u272F\u2606", "\u2605\u2605\u2605" +
                "\u2605\u2606", "\u2605" + "\u2605\u2605\u2605" + "\u272F",
                "\u2605\u2605\u2605\u2605" + "\u2605"};
        int maxStars = 5;

        // precondition
        assertEquals(ratings.length, expectedStrings.length);
        for (String expectedString : expectedStrings) {
            assertEquals(maxStars, expectedString.length());
        }

        // test
        for (int i = 0; i < expectedStrings.length; ++i) {
            assertEquals(expectedStrings[i],
                    Rateable.textRatingBar(ratings[i], maxStars));
        }
    }
}
