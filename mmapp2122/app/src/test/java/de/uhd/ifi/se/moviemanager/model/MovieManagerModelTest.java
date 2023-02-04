package de.uhd.ifi.se.moviemanager.model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;


class MovieManagerModelTest {

    private MovieManagerModel model = MovieManagerModel.getInstance();

    @AfterEach
    void tearDown() {
        model.clear();
    }

    @Test
    void testUniqueIdsOfMovies() {
        Movie movie = new Movie();
        model.addMovie(movie);
        Movie secondMovie = new Movie();
        model.addMovie(secondMovie);

        assertEquals(0, movie.getId());
        assertEquals(1, secondMovie.getId());
    }


}
