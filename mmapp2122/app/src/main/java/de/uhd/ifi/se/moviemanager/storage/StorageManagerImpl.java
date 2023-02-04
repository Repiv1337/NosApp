package de.uhd.ifi.se.moviemanager.storage;

import static android.graphics.Bitmap.createScaledBitmap;
import static java.lang.String.format;
import static de.uhd.ifi.se.moviemanager.util.FileUtils.walk;

import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.IntFunction;
import java.util.stream.Stream;

import de.uhd.ifi.se.moviemanager.model.ImagePyramid;
import de.uhd.ifi.se.moviemanager.model.Movie;
import de.uhd.ifi.se.moviemanager.model.MovieManagerModel;
import de.uhd.ifi.se.moviemanager.model.Performer;
import de.uhd.ifi.se.moviemanager.util.FileUtils;
import de.uhd.ifi.se.moviemanager.util.Log;

/**
 * Manages storage of data objects (e.g. of {@link Movie}s and {@link
 * Performer}s), of their associations, and images.
 * <p>
 * The Proxy design pattern is used: The class {@link StorageManagerAccess} is a
 * proxy that controls access to the {@link StorageManagerImpl} class. Both
 * classes implement the {@link StorageManager} interface.
 * <p>
 * Besides, this class uses the Multiton design pattern. The Multiton pattern
 * allows for the controlled creation of multiple instances, which it manages
 * through the use of a map.
 */
public class StorageManagerImpl implements StorageManager {
    private static final Map<File, StorageManager> INSTANCES = new HashMap<>();

    private final File homeDirectory;
    private final String imagePath;

    private MovieManagerModel movieManagerModel = MovieManagerModel
            .getInstance();

    private AssociationStorageManager<Movie, Performer> moviePerformerAssociationStorageManager;
    private DataObjectStorageManager<Movie> movieStorageManager;
    private DataObjectStorageManager<Performer> performerStorageManager;

    public static StorageManager getInstance(File file) {
        INSTANCES.computeIfAbsent(file, StorageManagerImpl::new);
        StorageManagerAccess.getInstance()
                .setStorageManager(INSTANCES.get(file));
        return INSTANCES.get(file);
    }

    private StorageManagerImpl(File homeDirectory) {
        super();
        this.homeDirectory = homeDirectory;
        imagePath = this.homeDirectory
                .getAbsoluteFile() + File.separator + "images";
        setup();
    }

    private void setup() {
        setupAssociationStorageManagers();
        setupDataObjectStorageManagers();
        loadDataObjectsFromJsonFiles();
        loadAssociationsFromFiles();
    }

    private void setupAssociationStorageManagers() {
        moviePerformerAssociationStorageManager =
                new AssociationStorageManager<>(
                        "movie-performer", this::tryGetMovieById,
                        this::tryGetPerformerById, homeDirectory);
    }

    private void setupDataObjectStorageManagers() {
        movieStorageManager = new DataObjectStorageManager<>(homeDirectory,
                Movie.class);
        performerStorageManager = new DataObjectStorageManager<>(homeDirectory,
                Performer.class);
    }

    private void loadDataObjectsFromJsonFiles() {
        List<Movie> storedMovies = movieStorageManager.loadFromStorage();
        movieManagerModel.getMovies().clear();
        movieManagerModel.getMovies().addAll(storedMovies);

        List<Performer> storedPerformers = performerStorageManager
                .loadFromStorage();
        movieManagerModel.getPerformers().clear();
        movieManagerModel.getPerformers().addAll(storedPerformers);
    }

    private void loadAssociationsFromFiles() {
        movieManagerModel.setMoviePerformerAssociations(
                moviePerformerAssociationStorageManager.loadFromStorage());
    }

    @Override
    public Movie saveMovieToFile(Movie movie) {
        movieStorageManager.saveToStorage(movie);
        saveAllAssociations();
        saveImage(movie.getImage());
        return movie;
    }

    private void saveAllAssociations() {
        moviePerformerAssociationStorageManager.saveToStorage(
                movieManagerModel.getMoviePerformerAssociations());
    }

    @Override
    public Performer savePerformerToFile(Performer performer) {
        performerStorageManager.saveToStorage(performer);
        moviePerformerAssociationStorageManager.saveToStorage(
                movieManagerModel.getMoviePerformerAssociations());
        saveImage(performer.getImage());
        return performer;
    }

    private Movie tryGetMovieById(int id) {
        return tryGetById(id, movieManagerModel::getMovieById, Movie.class);
    }

    private Performer tryGetPerformerById(int id) {
        return tryGetById(id, movieManagerModel::getPerformerById,
                Performer.class);
    }

    private <A> A tryGetById(int id, IntFunction<Optional<A>> getter,
                             Class<A> clazz) {
        Optional<A> optional = getter.apply(id);
        if (optional.isPresent()) {
            return optional.get();
        } else {
            Log.e("tryGetById", String.format("No %s with id=%d was found",
                    clazz.getSimpleName(), id));
        }
        return null;
    }

    @Override
    public boolean deleteMovieFile(Movie movie) {
        movieManagerModel
                .removeMovie(movie); // just in case it was not removed yet
        movieStorageManager.remove(movie);
        saveAllAssociations();
        removeImageFromStorage(movie.getImage());
        return true;
    }

    @Override
    public boolean deletePerformerFile(Performer performer) {
        movieManagerModel.removePerformer(
                performer); // just in case it was not removed yet
        performerStorageManager.remove(performer);
        moviePerformerAssociationStorageManager.saveToStorage(
                movieManagerModel.getMoviePerformerAssociations());
        removeImageFromStorage(performer.getImage());
        return true;
    }

    private void removeImageFromStorage(ImagePyramid imagePyramid) {
        imagePyramid.setBitmap((Bitmap) null);
        saveImage(imagePyramid);
    }

    @Override
    public void saveImage(ImagePyramid imagePyramid) {
        for (ImagePyramid.ImageSize size : ImagePyramid.ImageSize.values()) {
            String path = imagePyramid.getPath(size);
            File file = new File(path);
            if (imagePyramid.getBitmap() == null) {
                try {
                    FileUtils.delete(file);
                } catch (IOException e) {
                    Log.e("saveImage", format("Couldn't delete '%s'.", file));
                }
                continue;
            }
            Bitmap scaled = createScaledBitmap(imagePyramid.getBitmap(),
                    size.width, size.height, false);

            try {
                FileUtils.createDirectory(file);
            } catch (IOException e) {
                Log.e("saveImage",
                        format("Failed to create '%s'.", file.getParentFile()));
            }

            try (FileOutputStream out = new FileOutputStream(file)) {
                scaled.compress(Bitmap.CompressFormat.PNG, 100, out);
            } catch (IOException e) {
                Log.e("ImagePyramid", "Couldn't write size " + size);
                Log.e("ImagePyramid", "" + e);
            }
        }
    }

    @Override
    public String getImagePath() {
        return imagePath;
    }

    @Override
    public void clear() {
        setup();
    }

    @Override
    public void selfDestruct() {
        Stream<File> stream = walk(homeDirectory);
        stream.forEach(file -> {
            try {
                FileUtils.delete(file);
            } catch (IOException e) {
                Log.e("selfDestruct", "Stored files could not be deleted.");
            }
        });
    }
}

