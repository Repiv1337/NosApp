package de.uhd.ifi.se.moviemanager.model.movie;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import de.uhd.ifi.se.moviemanager.model.Movie;
import de.uhd.ifi.se.moviemanager.model.MovieManagerModel;
import de.uhd.ifi.se.moviemanager.model.MovieRelease;

class MovieReleaseTest {

    private MovieManagerModel model = MovieManagerModel.getInstance();

    @AfterEach
    void tearDown() {
        model.clear();
    }

    @Test
    void testMovieHasNoReleases() {
        Movie movie = new Movie("Guardians of the Galaxy 5");
        assertEquals(0, movie.getReleases().size());
    }

    @Test
    void testMovieHasOneRelease() throws ParseException {
        Movie movie = new Movie("Guardians of the Galaxy");

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date releaseDate = formatter.parse("2014-08-28");

        MovieRelease release = new MovieRelease("Germany", releaseDate);
        movie.addRelease(release);

        assertEquals(1, movie.getReleases().size());
    }

    @Test
    void testMovieHasTwoReleases() throws ParseException {
        Movie movie = new Movie("Guardians of the Galaxy");

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date releaseDate = formatter.parse("2014-08-28");

        List<MovieRelease> releases = new ArrayList<>();
        MovieRelease release = new MovieRelease("Germany", releaseDate);
        releases.add(new MovieRelease("Germany", releaseDate));
        Date newReleaseDate = formatter.parse("2014-08-01");
        releases.add(new MovieRelease("US", releaseDate));

        movie.setReleases(releases);
        assertEquals(2, movie.getReleases().size());
    }

    @Test
    void testMovieReleaseClass() throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date releaseDate = formatter.parse("2014-08-28");
        MovieRelease release = new MovieRelease("Germany", releaseDate);

        assertEquals("Germany", release.getLocation());
        assertEquals(releaseDate, release.getDate());

        release.setLocation("US");
        Date newReleaseDate = formatter.parse("2014-08-01");
        release.setDate(newReleaseDate);

        assertEquals("US", release.getLocation());
        assertEquals(newReleaseDate, release.getDate());
    }

}
