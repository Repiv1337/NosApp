package de.uhd.ifi.se.moviemanager.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.google.common.collect.Multimap;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.nio.file.Paths;

import de.uhd.ifi.se.moviemanager.model.Movie;
import de.uhd.ifi.se.moviemanager.model.MovieManagerModel;
import de.uhd.ifi.se.moviemanager.model.Performer;

class AssociationStorageManagerTest {

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
    void testMoviePerformerAssociationSuccessful() {
        Performer performer = new Performer();
        model.addPerformer(performer);
        Movie movie = new Movie();
        model.addMovie(movie);
        performer.link(movie);

        AssociationStorageManager<Movie, Performer> storageManager =
                new AssociationStorageManager(
                "movie-performer", id -> model.getMovieById((Integer) id).get(),
                id -> model.getPerformerById((Integer) id).get(),
                HOME.toFile());
        storageManager.saveToStorage(model.getMoviePerformerAssociations());
        Multimap<Movie, Performer> mapReloaded = storageManager
                .loadFromStorage();

        assertEquals(1, mapReloaded.size());
        assertEquals(model.getMoviePerformerAssociations(), mapReloaded);
    }

    @Test
    void testMoviePerformerAssociationObjectCannotBeFound() {
        Performer performer = new Performer();
        Movie movie = new Movie();
        performer.link(movie);

        AssociationStorageManager<Movie, Performer> storageManager =
                new AssociationStorageManager(
                "movie-performer", id -> model.getMovieById((Integer) id).get(),
                id -> model.getPerformerById((Integer) id).get(),
                HOME.toFile());
        storageManager.saveToStorage(model.getMoviePerformerAssociations());
        model.removeMovie(movie);

        Multimap<Movie, Performer> mapReloaded = storageManager
                .loadFromStorage();

        assertEquals(0, mapReloaded.size());
        assertEquals(model.getMoviePerformerAssociations(), mapReloaded);
    }
}
