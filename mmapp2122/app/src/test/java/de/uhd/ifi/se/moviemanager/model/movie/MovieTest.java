package de.uhd.ifi.se.moviemanager.model.movie;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.uhd.ifi.se.moviemanager.model.Movie;
import de.uhd.ifi.se.moviemanager.model.MovieRelease;
import de.uhd.ifi.se.moviemanager.util.DateUtils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * Tests basic methods such as equals, getters and setters of {@link
 * de.uhd.ifi.se.moviemanager.model.Movie} class.
 */
class MovieTest {

    @Test
    void testTitleAndDescription() {
        Movie movie = new Movie("Guardians of the Galaxy 3");
        movie.setDescription("a 2014 American superhero film based on the Marvel " +
                "Comics superhero team of the same name.");

        assertEquals("Guardians of the Galaxy 3", movie.getTitle());
        assertFalse(movie.getDescription().isEmpty());
    }

    @Test
    void testRatingAndRuntime() {
        Movie movie = new Movie();
        movie.setRating(4.5);
        movie.setRuntime(120);

        assertEquals(4.5, movie.getRating());
        assertEquals(120, movie.getRuntime());
    }

    @Test
    void testWatchDate() {
        Movie movie = new Movie();
        Date today = DateUtils.nowAtMidnight();
        movie.setWatchDate(today);

        assertEquals(today, movie.getWatchDate());
    }

    @Test
    void testLanguages() {
        Movie movie = new Movie();
        List<String> languages = new ArrayList<>();
        languages.add("German");
        languages.add("English");
        movie.setLanguages(languages);

        assertEquals(languages, movie.getLanguages());
    }

    @Test
    void testReleases() {
        Movie movie = new Movie();
        List<MovieRelease> releases = new ArrayList<>();
        releases.add(new MovieRelease("USA", new Date()));
        releases.add(new MovieRelease("Australia", new Date(DateUtils.nowAtMidnight().getTime() + 31 * 24 * 60 * 60 * 1000L)));
        movie.setReleases(releases);

        assertEquals(releases, movie.getReleases());
    }

    @Test
    void testProductionLocations() {
        Movie movie = new Movie();
        List<String> productionLocations = new ArrayList<>();
        productionLocations.add("USA");
        productionLocations.add("Australia");
        movie.setProductionLocations(productionLocations);

        assertEquals(productionLocations, movie.getProductionLocations());
    }
}
