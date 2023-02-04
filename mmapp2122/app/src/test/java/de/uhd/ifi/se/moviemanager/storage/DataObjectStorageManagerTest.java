package de.uhd.ifi.se.moviemanager.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import de.uhd.ifi.se.moviemanager.model.Movie;
import de.uhd.ifi.se.moviemanager.model.MovieManagerModel;

class DataObjectStorageManagerTest {

    private StorageManager storage;
    private static final Path HOME = Paths.get("TestRuntimeStorage");
    private MovieManagerModel model = MovieManagerModel.getInstance();

    @BeforeEach
    void init() {
        storage = StorageManagerImpl.getInstance(HOME.toFile());
        MovieManagerModel.getInstance().clear();
        storage.clear();
    }

    @AfterEach
    void tearDown() {
        model.clear();
        storage.selfDestruct();
    }

    @Test
    void testMovieStorageManagerSaveAndReload() {
        Movie movie = new Movie();

        DataObjectStorageManager<Movie> storageManager =
                new DataObjectStorageManager<>(
                HOME.toFile(), Movie.class);
        storageManager.saveToStorage(movie);

        Optional<Movie> movieReloaded = storageManager.loadFromStorage(
                new File(HOME + File.separator + "movies/movie_0.json"));

        assertTrue(movieReloaded.isPresent());
        assertEquals(movie, movieReloaded.get());
    }

    @Test
    void testMovieStorageManagerRemove() {
        Movie movie = new Movie();

        DataObjectStorageManager<Movie> storageManager =
                new DataObjectStorageManager<>(
                HOME.toFile(), Movie.class);
        storageManager.saveToStorage(movie);
        storageManager.remove(movie);

        Optional<Movie> movieReloaded = storageManager.loadFromStorage(
                new File(HOME + File.separator + "movies/movie_0.json"));
        assertFalse(movieReloaded.isPresent());

        // movie was deleted in JSON file but not in RAM (movie manager model)
        assertNotNull(movie);
    }
}
