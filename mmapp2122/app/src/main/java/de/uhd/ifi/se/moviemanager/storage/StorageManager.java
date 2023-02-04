package de.uhd.ifi.se.moviemanager.storage;

import de.uhd.ifi.se.moviemanager.model.ImagePyramid;
import de.uhd.ifi.se.moviemanager.model.Movie;
import de.uhd.ifi.se.moviemanager.model.MovieManagerModel;
import de.uhd.ifi.se.moviemanager.model.Performer;

/**
 * Interface for managing storage of data objects (e.g. of {@link Movie}s and {@link Performer}s), of their
 * associations, and of images.
 * <p>
 * The Proxy design pattern is used: The class {@link StorageManagerAccess} is a proxy that controls
 * access to the {@link StorageManagerImpl} class. Both classes implement the {@link StorageManager}
 * interface.
 */
public interface StorageManager {

    /**
     * Saves the {@link Movie} object to a JSON file. This method is also used for updating an
     * existing file. For updating, the id of the {@link Movie} object needs to be the same as the
     * file id.
     *
     * @param movie {@link Movie} object.
     * @return {@link Movie} object with id >= 0 if saving was successful.
     */
    Movie saveMovieToFile(Movie movie);

    /**
     * Saves the {@link Performer} object to a JSON file. This method is also used for updating an
     * existing file. For updating, the id of the {@link Performer} object needs to be the same as
     * the file id.
     *
     * @param performer {@link Performer} object.
     * @return {@link Performer} object with id >= 0 if saving was successful.
     */
    Performer savePerformerToFile(Performer performer);

    /**
     * Deletes a movie_id.json file in file system.
     *
     * @param movie {@link Movie} object.
     * @return true if deletion was successful.
     */
    boolean deleteMovieFile(Movie movie);

    /**
     * Deletes a performer_id.json file in file system.
     *
     * @param performer {@link Performer} object.
     * @return true if deletion was successful.
     */
    boolean deletePerformerFile(Performer performer);

    String getImagePath();

    /**
     * Save a png file in the movie_manager/images folder in three different sizes (small, medium,
     * and large).
     *
     * @param imagePyramid image in three different sizes (small, medium, and large).
     */
    void saveImage(ImagePyramid imagePyramid);

    /**
     * Use carefully! Reloads the whole {@link MovieManagerModel} from storage. Deletes all
     * temporary data that was not saved in files.
     */
    void clear();

    /**
     * Use carefully! Deletes all files and directories in the file system.
     */
    void selfDestruct();
}
