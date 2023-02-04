package de.uhd.ifi.se.moviemanager.model.movie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import de.uhd.ifi.se.moviemanager.model.Movie;
import de.uhd.ifi.se.moviemanager.model.MovieManagerModel;
import de.uhd.ifi.se.moviemanager.model.Performer;

class CalculateOverallRatingTest {

    private MovieManagerModel model = MovieManagerModel.getInstance();

    @AfterEach
    void tearDown() {
        model.clear();
    }

    @Test
    @DisplayName("Overall Rating - uninitialized Movie")
    void testCalculateOverallRatingWithUninitializedMovie() {
        // precondition
        Movie movie = null; // movie exists

        // test
        Assertions.assertThrows(NullPointerException.class, () -> {
            movie.calculateOverallRating();
        });
    }

    @Test
    @DisplayName("Overall Rating - unrated Movie")
    void testCalculateOverallRatingWithUnratedMovie() {
        // precondition
        Movie movie = new Movie("The Circle"); // movie exists
        assertFalse(movie.isRated());  // movie is unrated

        // the number of linked performers does not matter

        // test
        assertEquals(-1.f, movie.calculateOverallRating());

        // postcondition
        assertFalse(movie.isRated()); // nothing changed, movie is still unrated
    }

    /**
     * Creates a list of performers with given ratings.
     *
     * @param ratings zero or more performer ratings.
     * @return list of performers with unique ids and ratings set.
     */
    private static List<Performer> createPerformersWithRating(
            double... ratings) {
        if (ratings.length == 0) {
            return Collections.emptyList();
        }

        List<Performer> performers = IntStream.range(0, ratings.length)
                .mapToObj(i -> createRatedPerformer(i, ratings[i]))
                .collect(Collectors.toList());

        return performers;
    }

    private static Performer createRatedPerformer(int id, double rating) {
        Performer performer = new Performer(id);
        performer.setRating(rating);
        return performer;
    }

    @Test
    @DisplayName("Overall Rating - rated Movie - no Performers")
    void testCalculateOverallRatingWithRatedMovieAndNoPerformers() {
        // precondition
        Movie movie = new Movie(0); // movie exists
        movie.setRating(3.5);
        assertTrue(movie.isRated()); // movie is rated

        List<Performer> performers = new ArrayList<>();
        movie.link(performers);
        assertTrue(movie.getPerformers()
                .isEmpty()); // movie has no linked performers

        // test
        assertEquals(3.5, movie.calculateOverallRating());

        // postcondition
        assertTrue(movie.isRated()); // nothing changed, movie is still rated
    }

    @Test
    @DisplayName("Overall Rating - rated Movie - one rated Performer")
    void testCalculateOverallRatingWithRatedMovieAndOneRatedPerformer() {
        // precondition
        Movie movie = new Movie();
        movie.setRating(3); // movie exists
        assertTrue(movie.isRated()); // movie is rated

        Performer performer = new Performer();
        performer.setRating(4);
        movie.link(performer);
        assertFalse(movie.getPerformers()
                .isEmpty()); // linked performers exist and are rated

        // test
        assertEquals(3.5, movie.calculateOverallRating());

        // postcondition
        assertTrue(movie.isRated()); // nothing changed, movie is still rated
    }

    @Test
    @DisplayName("Overall Rating - rated Movie - multiple rated Performers")
    void testCalculateOverallRatingWithRatedMovieAndMultipleRatedPerformers() {
        // precondition
        Movie movie = new Movie(0);
        movie.setRating(3.5); // movie exists
        assertTrue(movie.isRated()); // movie is rated

        List<Performer> performers = createPerformersWithRating(4.5, 2.5, 1.5,
                5.0);
        movie.link(performers);
        assertFalse(movie.getPerformers()
                .isEmpty()); // linked performers exist and are rated

        // test
        assertEquals(3.4375, movie.calculateOverallRating());

        // postcondition
        assertTrue(movie.isRated()); // nothing changed, movie is still rated
    }

    @Test
    @DisplayName("Overall Rating - rated Movie - one unrated Performer")
    void testCalculateOverallRatingWithRatedMovieAndOneUnratedPerformer() {
        // precondition
        Movie movie = new Movie();
        movie.setRating(3); // movie exists
        assertTrue(movie.isRated()); // movie is rated

        Performer performer = new Performer();
        movie.link(performer);
        assertFalse(movie.getPerformers()
                .isEmpty()); // linked performers exist and are unrated

        // test
        assertEquals(3, movie.calculateOverallRating());

        // postcondition
        assertTrue(movie.isRated()); // nothing changed, movie is still rated
    }

    @Test
    @DisplayName("Overall Rating - rated Movie - multiple unrated Performers")
    void testCalculateOverallRatingWithRatedMovieAndMultipleUnratedPerformers() {
        // precondition
        Movie movie = new Movie(0); // movie exists
        movie.setRating(3.5);
        assertTrue(movie.isRated()); // movie is rated

        List<Performer> performers = createPerformersWithRating(-4.5, -2.5,
                -1.5, -5.0);
        movie.link(performers);
        assertFalse(movie.getPerformers()
                .isEmpty()); // movie has linked performers and they are unrated

        // test
        assertEquals(3.5, movie.calculateOverallRating());

        // postcondition
        assertTrue(movie.isRated()); // nothing changed, movie is still rated
    }

    @Test
    @DisplayName("Overall Rating - rated Movie - multiple rated and unrated " + "Performers")
    void testCalculateOverallRatingWithRatedMovieAndMultipleRatedAndUnratedPerformers() {
        // precondition
        Movie movie = new Movie(0); // movie exists
        movie.setRating(3.5);
        assertTrue(movie.isRated()); // movie is rated

        List<Performer> performers = createPerformersWithRating(4.5, -2.5, 1.5,
                -5.0);
        movie.link(performers);
        assertFalse(movie.getPerformers()
                .isEmpty()); // movie has linked performers and they are
        // rated or unrated

        // test
        assertEquals(3.25, movie.calculateOverallRating());

        // postcondition
        assertTrue(movie.isRated()); // nothing changed, movie is still rated
    }
}
