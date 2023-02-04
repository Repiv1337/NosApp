package de.uhd.ifi.se.moviemanager.model.performer;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import de.uhd.ifi.se.moviemanager.model.Performer;
import de.uhd.ifi.se.moviemanager.util.DateUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * <p>
 * Tests basic methods such as equals, getters and setters of {@link
 * de.uhd.ifi.se.moviemanager.model.Performer} class.
 * </p>
 */
class PerformerTest {

    @Test
    void testEquality() {
        Performer p1 = createNewPerformer(0, "Performer1");
        Performer p2 = createNewPerformer(0, "Performer1");

        assertEquals(p1, p2);
    }

    @Test
    void testNameAndBirthName() {
        Performer p = createNewPerformer(0, "Performer"  );
        p.setBirthName( "Test Performer" );

        assertEquals("Performer", p.getFirstName());
        assertEquals( Collections.emptyList(), p.getMiddleNames());
        assertEquals("Performer", p.getLastName());
        assertEquals("Test Performer", p.getBirthName());
    }

    @Test
    void testFirstNameMiddleNameAndLastName() {
        Performer p = createNewPerformer(0, "Julia Scarlett Elizabeth Louis-Dreyfuss");

        assertEquals("Julia", p.getFirstName());
        assertEquals(Arrays.asList("Scarlett", "Elizabeth"), p.getMiddleNames());
        assertEquals("Louis-Dreyfuss", p.getLastName() );
    }

    @Test
    void testAge() {
        Performer p = createNewPerformer(0, "Performer"  );

        assertEquals(0, p.getAge());

        Calendar age = Calendar.getInstance();
        //sets age (birthday) 21 years before current day
        age.set(age.get(Calendar.YEAR)-21, age.get(Calendar.MONTH), age.get(Calendar.DAY_OF_MONTH));

        p.setDateOfBirth(age.getTime());

        assertEquals(21, p.getAge());
    }

    @Test
    void testDateOfBirth() {
        Performer p = createNewPerformer(0, "Performer"  );
        Date today = DateUtils.nowAtMidnight();
        p.setDateOfBirth(today);

        assertEquals(today, p.getDateOfBirth());
    }

    @Test
    void testRating() {
        Performer p = createNewPerformer(0, "Performer"  );
        p.setRating( 5.0 );

        assertEquals(5, p.getRating());
    }

    @Test
    void testBiography() {
        Performer p = createNewPerformer(0, "Jared Leto"  );
        p.setBiography("is an American actor and singer-songwriter. After starting his career with television appearances " +
                "in the early 1990s, Leto achieved recognition for his role " +
                "as Jordan Catalano on the television series My So-Called Life (1994).");

        assertFalse(p.getBiography().isEmpty());
    }

    @Test
    void testOccupations() {
        Performer p = createNewPerformer(0, "Performer");
        List<String> occupations = new ArrayList<>( );
        occupations.add("Actor");
        occupations.add("Test Subject");
        p.setOccupations(occupations );

        assertEquals( occupations, p.getOccupations() );
    }

    private Performer createNewPerformer(int id, String name) {
        Performer performer = new Performer(id);
        performer.setName(name);
        return performer;
    }
}
