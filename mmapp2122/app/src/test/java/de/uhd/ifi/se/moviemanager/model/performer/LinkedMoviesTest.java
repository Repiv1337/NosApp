package de.uhd.ifi.se.moviemanager.model.performer;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import de.uhd.ifi.se.moviemanager.model.Movie;
import de.uhd.ifi.se.moviemanager.model.MovieManagerModel;
import de.uhd.ifi.se.moviemanager.model.Performer;

class LinkedMoviesTest {

    private MovieManagerModel model = MovieManagerModel.getInstance();

    @AfterEach
    void tearDown() {
        model.clear();
    }

    @Test
    void testNoMovieLinkedToPerformer() {
        Performer performer = new Performer(); // performer exists
        assertEquals(0,
                performer.getMovies().size()); // performer has no linked movies
        assertFalse(performer.hasOneOrMoreMoviesLinked());
    }

    @Test
    void testOneUninitialisedMovieLinkedToPerformer() {
        Performer performer = new Performer(); // performer exists

        assertFalse(performer.link((Movie) null));
        assertEquals(0,
                performer.getMovies().size()); // performer has no linked movies

    }

    @Test
    void testOneMovieLinkedToPerformer() {
        Performer performer = new Performer(); // performer exists
        Movie movie = new Movie();
        performer.link(movie); // performer has one movie linked

        assertEquals(1, performer.getMovies().size());
        assertEquals(1, movie.getPerformers().size());
        assertTrue(performer.hasOneOrMoreMoviesLinked());
        assertTrue(performer.isPerformerIn(movie));
    }

    @Test
    void testTwoMoviesLinkedToPerformer() {
        Performer performer = new Performer();
        model.addPerformer(performer);

        Movie movie = new Movie(); // movie exists
        model.addMovie(movie);
        Movie anotherMovie = new Movie();
        model.addMovie(anotherMovie);

        performer.link(movie);
        performer.link(anotherMovie); // performer has two linked movies

        assertEquals(2, performer.getMovies().size());
        assertEquals(1, movie.getPerformers().size());
        assertEquals(1, anotherMovie.getPerformers().size());
    }

    @Test
    void testOneMovieLinkedToPerformerLinkedTwice() {
        Performer performer = new Performer(); // performer exists
        Movie movie = new Movie();
        performer.link(movie); // performer has one movie linked
        performer.link(movie);
        movie.link(performer); // the performer should only be linked once

        assertEquals(1, performer.getMovies().size());
        assertEquals(1, movie.getPerformers().size());
    }

    @Test
    void testUnlinkMovie() {
        Performer performer = new Performer(); // performer exists
        Movie movie = new Movie();
        performer.link(movie); // performer has one movie linked

        assertEquals(1, performer.getMovies().size());
        assertEquals(1, movie.getPerformers().size());

        performer.unlink(movie);
        assertFalse(performer.hasOneOrMoreMoviesLinked());
        assertEquals(0, movie.getPerformers().size());
        assertEquals(0, performer.getMovies().size());
    }

    @Test
    void testUnlinkMovieNotLinkedToPerformer() {
        Performer performer = new Performer(); // performer exists
        Movie movie = new Movie(); // movie not linked to performer exists

        assertFalse(performer.unlink(movie));
        assertEquals(0, performer.getMovies().size());
    }

    @Test
    void testUnlinkInitializedPerformer() {
        Performer performer = new Performer(); // performer exists

        assertFalse(performer.unlink((Movie) null));
        assertEquals(0, performer.getMovies().size());
    }
}
