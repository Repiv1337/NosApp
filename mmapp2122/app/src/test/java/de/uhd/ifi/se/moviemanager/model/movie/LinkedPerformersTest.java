package de.uhd.ifi.se.moviemanager.model.movie;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import de.uhd.ifi.se.moviemanager.model.Movie;
import de.uhd.ifi.se.moviemanager.model.MovieManagerModel;
import de.uhd.ifi.se.moviemanager.model.Performer;

class LinkedPerformersTest {

    private MovieManagerModel model = MovieManagerModel.getInstance();

    @AfterEach
    void tearDown() {
        model.clear();
    }

    @Test
    void testNoPerformerLinkedToMovie() {
        Movie movie = new Movie(); // movie exists
        assertEquals(0,
                movie.getPerformers().size()); // movie has no linked performer
    }

    @Test
    void testOneUninitializedPerformerLinkedToMovie() {
        Movie movie = new Movie(); // movie exists

        assertFalse(movie.link((Performer) null));

        assertEquals(0, movie.getPerformers().size());
    }

    @Test
    void testOnePerformerLinkedToMovie() {
        Movie movie = new Movie(); // movie exists
        Performer performer = new Performer(); // movie has one linked performer
        movie.link(performer);

        assertEquals(1, movie.getPerformers().size());
        assertEquals(1, performer.getMovies().size());
        assertTrue(movie.hasPerformer(performer));
    }

    @Test
    void testTwoPerformersLinkedToMovie() {
        Movie movie = new Movie(); // movie exists
        model.addMovie(movie);
        Performer performer = new Performer();
        model.addPerformer(performer);
        Performer anotherPerformer = new Performer();
        model.addPerformer(anotherPerformer);

        movie.link(performer);
        movie.link(anotherPerformer); // movie has two linked performers

        assertEquals(2, movie.getPerformers().size());
        assertEquals(1, performer.getMovies().size());
        assertEquals(1, anotherPerformer.getMovies().size());
    }

    @Test
    void testOnePerformerLinkedToMovieLinkedTwice() {
        Movie movie = new Movie(); // movie exists
        Performer performer = new Performer(); // movie has one linked performer
        movie.link(performer);
        movie.link(performer); // the performer should only be linked once

        assertEquals(1, movie.getPerformers().size());
        assertEquals(1, performer.getMovies().size());
    }

    @Test
    void testUnlinkPerformer() {
        Movie movie = new Movie(); // movie exists
        Performer performer = new Performer(); // movie has one linked performer
        movie.link(performer);

        assertEquals(1, movie.getPerformers().size());
        assertEquals(1, performer.getMovies().size());

        movie.unlink(performer);
        assertEquals(0, movie.getPerformers().size());
        assertEquals(0, performer.getMovies().size());
    }

    @Test
    void testUnlinkPerformerNotLinkedToMovie() {
        Movie movie = new Movie(); // movie exists
        Performer performer = new Performer(); // performer not linked to
        // movie exists

        assertFalse(movie.unlink(performer));
        assertEquals(0, movie.getPerformers().size());
    }

    @Test
    void testUnlinkInitializedPerformer() {
        Movie movie = new Movie(); // movie exists

        assertFalse(movie.unlink((Performer) null));
        assertEquals(0, movie.getPerformers().size());
    }
}
