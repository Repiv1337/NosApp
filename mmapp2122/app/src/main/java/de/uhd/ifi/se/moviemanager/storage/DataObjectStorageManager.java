package de.uhd.ifi.se.moviemanager.storage;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Multimap;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

import de.uhd.ifi.se.moviemanager.model.Identifiable;
import de.uhd.ifi.se.moviemanager.model.Movie;
import de.uhd.ifi.se.moviemanager.model.Performer;
import de.uhd.ifi.se.moviemanager.util.FileUtils;
import de.uhd.ifi.se.moviemanager.util.Log;

/**
 * Manages the persistence of single data objects, e.g., of {@link Movie}s and
 * {@link Performer}s.
 */
public class DataObjectStorageManager<T extends Identifiable> {

    // class of the objects to be stored, e.g. Movie.class
    private Class<T> modelClass;

    // directory in which the JSON files are stored, e.g. "movie_manager/movies"
    private File directory;

    // contains all JSON files, e.g. "movie_0.json, movie_1.json, ..."
    private Set<File> files;

    public DataObjectStorageManager(File homeDirectory, Class<T> modelClass) {
        this.modelClass = modelClass;
        directory = new File(
                homeDirectory + File.separator + getObjectPrefix() + "s");
        directory.mkdirs();
        files = new HashSet<>();
    }

    /**
     * @return e.g. "movie" for the Movie.class
     */
    private String getObjectPrefix() {
        return modelClass.getSimpleName().toLowerCase();
    }

    /**
     * @param object e.g. {@link Movie} or {@link Performer} object to be saved
     *               in a JSON file.
     */
    public void saveToStorage(T object) {
        String fileName = createFileNameFor(object);
        File file = new File(directory + File.separator + fileName);
        try {
            file.createNewFile();
            ObjectMapper mapper = getObjectMapper();
            mapper.writerWithDefaultPrettyPrinter().writeValue(file, object);
        } catch (IOException e) {
            Log.e("saveToStorage", "" + e);
        }
    }

    /**
     * @return configured {@link ObjectMapper} to write and read data objects
     * from JSON files.
     */
    private ObjectMapper getObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setDateFormat(getHumanReadableDateFormat());
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                false);
        return mapper;
    }

    /**
     * Makes the date human readable in the JSON file. Used by the JSON object
     * mapper.
     *
     * @return human readable {@link DateFormat}, e.g. "Fri Jun 29 00:00:00 GMT
     * 2018".
     */
    private DateFormat getHumanReadableDateFormat() {
        return new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", Locale.US);
    }

    /**
     * @param object of data class, e.g. {@link Movie} or {@link Performer}
     *               object.
     * @return e.g. "movie_0.json" for the first object in the set of {@link
     * Movie}s.
     */
    private String createFileNameFor(T object) {
        return getObjectPrefix() + "_" + object.getId() + ".json";
    }

    public List<T> loadFromStorage() {
        loadNamesForAllStoredClasses();
        List<T> objects = new ArrayList<>();
        for (File jsonFile : files) {
            Optional<T> opt = loadFromStorage(jsonFile);
            if (opt.isPresent()) {
                objects.add(opt.get());
            }
        }
        return objects;
    }

    private void loadNamesForAllStoredClasses() {
        if (!directory.exists()) {
            return;
        }

        for (File file : directory.listFiles()) {
            if (file.isFile() && file.getName().matches(".*\\.json")) {
                files.add(file);
            }
        }
    }

    /**
     * @param jsonFile of a data object, e.g. movie_1.json.
     * @return optional containing the data object (e.g. {@link Movie} object)
     * if present.
     */
    protected Optional<T> loadFromStorage(File jsonFile) {
        ObjectMapper mapper = getObjectMapper();
        try {
            T object = (T) mapper.readValue(jsonFile, Identifiable.class);
            return Optional.of(object);
        } catch (IOException je) {
            return Optional.empty();
        }
    }

    /**
     * Removes the JSON file from storage. Does not remove associations (see
     * {@link AssociationStorageManager#saveToStorage(Multimap)}.
     *
     * @param object of data class, e.g. {@link Movie} or {@link Performer}
     *               object.
     */
    public void remove(T object) {
        String fileName = createFileNameFor(object);
        File file = new File(directory + File.separator + fileName);
        files.remove(file);
        try {
            FileUtils.delete(file);
        } catch (IOException e) {
            Log.e("remove", object + " could not be deleted. " + e);
        }
    }
}