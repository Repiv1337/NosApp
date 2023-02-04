package de.uhd.ifi.se.moviemanager.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static java.util.Collections.emptyList;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

import de.uhd.ifi.se.moviemanager.model.Address;
import de.uhd.ifi.se.moviemanager.model.Movie;
import de.uhd.ifi.se.moviemanager.model.MovieManagerModel;
import de.uhd.ifi.se.moviemanager.model.MovieRelease;
import de.uhd.ifi.se.moviemanager.model.Performer;


class StorageManagerTest {
    private static final Path HOME = Paths.get("TestRuntimeStorage");
    private StorageManager storage;
    private MovieManagerModel model = MovieManagerModel.getInstance();


    @BeforeEach
    void init() {
        storage = StorageManagerImpl.getInstance(HOME.toFile());
        storage.clear();
        MovieManagerModel.getInstance().clear();
    }

    @AfterEach
    void tearDown() {
        storage.selfDestruct();
        MovieManagerModel.getInstance().clear();
    }

    @Test
    void testMovieFileCreation() {
        Movie movie = new Movie("Movie");
        Path moviePath = HOME.resolve("movies").resolve("movie_0.json");
        assertFalse(moviePath.toFile().exists());

        // test
        storage.saveMovieToFile(movie);
        assertTrue(moviePath.toFile().exists());
    }

    @Test
    void testPerformerFileCreation() {
        Performer performer = new Performer("Performer");
        Path performerPath = HOME.resolve("performers")
                .resolve("performer_0.json");
        assertFalse(performerPath.toFile().exists());

        // test
        storage.savePerformerToFile(performer);
        assertTrue(performerPath.toFile().exists());
    }

    @Test
    void testWrittenMovieFilesEquality() throws Throwable {
        Movie movie1 = storage.saveMovieToFile(new Movie("The Movie"));
        model.addMovie(movie1);

        Movie movie2 = new Movie();
        model.addMovie(movie2);

        movie2.setTitle("Movie");
        movie2.setRuntime(130);
        List<MovieRelease> releases = new ArrayList<>();
        releases.add(new MovieRelease("Germany", new Date()));
        movie2.setReleases(releases);
        storage.saveMovieToFile(movie2);

        // precondition
        assertEquals(0, movie1.getId());
        assertEquals(1, movie2.getId());

        // test
        Movie m1R = readMovieFromFile("movie_0.json");
        Movie m2R = readMovieFromFile("movie_1.json");

        assertEquals(movie1, m1R);
        assertEquals(movie2, m2R);
    }

    private Movie readMovieFromFile(String fName) throws Exception {
        Path path = HOME.resolve("movies").resolve(fName);
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(
                new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy",
                        Locale.US));
        return mapper.readValue(path.toFile(), Movie.class);
    }

    @Test
    void testWrittenPerformerFilesEquality() throws Throwable {
        Performer p1 = storage.savePerformerToFile(new Performer());
        model.addPerformer(p1);

        Performer p2 = storage.savePerformerToFile(new Performer());
        model.addPerformer(p2);

        // precondition
        assertEquals(0, p1.getId());
        assertEquals(1, p2.getId());

        // test
        Performer p1R = readPerformerFromFile("performer_0.json");
        Performer p2R = readPerformerFromFile("performer_1.json");

        assertEquals(p1, p1R);
        assertEquals(p2, p2R);
    }

    private Performer readPerformerFromFile(String fName) throws Exception {
        Path path = HOME.resolve("performers").resolve(fName);
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readValue(path.toFile(), Performer.class);
    }

    @Test
    void testRemoveMovie() {
        Movie movie = storage.saveMovieToFile(new Movie());
        Path moviePath = HOME.resolve("movies").resolve("movie_0.json");
        assertTrue(moviePath.toFile().exists());

        storage.deleteMovieFile(movie);
        assertFalse(moviePath.toFile().exists());

        assertEquals(0, model.getMovies().size());
    }

    @Test
    void testRemovePerformer() {
        Movie movie = storage.saveMovieToFile(new Movie());
        model.addMovie(movie);

        Performer performer = new Performer();
        model.addPerformer(performer);

        performer.link(movie);
        storage.savePerformerToFile(performer);

        Path moviePath = HOME.resolve("movies").resolve("movie_0.json");
        assertTrue(moviePath.toFile().exists());
        Path performerPath = HOME.resolve("performers")
                .resolve("performer_0.json");
        assertTrue(performerPath.toFile().exists());

        assertTrue(movie.hasPerformer(performer));
        assertFalse(model.getPerformers().isEmpty());

        // test
        storage.deletePerformerFile(performer);
        assertTrue(model.getPerformers().isEmpty());
        assertFalse(model.getMovies().isEmpty());
    }

    @Test
    void testUpdateMovie() {
        // setup
        Movie m = storage.saveMovieToFile(new Movie());
        Movie expectedRaw = new Movie(0);
        Movie expectedUpdated = new Movie(0);
        expectedUpdated.setTitle("Updated");

        // precondition
        assertNotEquals(expectedUpdated, m);
        assertEquals(expectedRaw, m);

        // test
        m.setTitle("Updated");
        m = storage.saveMovieToFile(m);
        assertEquals(expectedUpdated, m);
        assertNotEquals(expectedRaw, m);
    }

    @Test
        // model
    void testMovieReload() throws Throwable {
        Movie m1 = storage.saveMovieToFile(new Movie());
        model.addMovie(m1);
        Movie m1R = readMovieFromFile("movie_0.json");

        // precondition
        assertFalse(model.getMovies().isEmpty());
        assertEquals(m1, m1R);
        assertNotSame(m1, m1R);

        // test
        Optional<Movie> m1cOpt = model.getMovieById(0);
        assertTrue(m1cOpt.isPresent());
        Movie m1c = m1cOpt.get();
        assertEquals(m1, m1c);
        assertSame(m1, m1c);
    }

    @Test
    void testPerformerReload() throws Throwable {
        Performer p1 = storage.savePerformerToFile(new Performer());
        model.addPerformer(p1);
        Performer p1R = readPerformerFromFile("performer_0.json");

        // precondition
        assertFalse(model.getPerformers().isEmpty());
        assertEquals(p1, p1R);
        assertNotSame(p1, p1R);

        // test
        Optional<Performer> p1cOpt = model.getPerformerById(0);
        assertTrue(p1cOpt.isPresent());
        Performer p1C = p1cOpt.get();
        assertEquals(p1, p1C);
        assertSame(p1, p1C);
    }

    private Movie setupMovie(String title) {
        Movie movie = new Movie();
        movie.setTitle(title);
        model.addMovie(movie);
        return storage.saveMovieToFile(movie);
    }
}
