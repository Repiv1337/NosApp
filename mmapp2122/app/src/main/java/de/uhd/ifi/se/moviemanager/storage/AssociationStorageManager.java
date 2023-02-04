package de.uhd.ifi.se.moviemanager.storage;

import static java.util.Arrays.asList;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import org.apache.commons.lang3.StringUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Function;

import de.uhd.ifi.se.moviemanager.model.Identifiable;
import de.uhd.ifi.se.moviemanager.model.Movie;
import de.uhd.ifi.se.moviemanager.model.Performer;
import de.uhd.ifi.se.moviemanager.util.FileUtils;
import de.uhd.ifi.se.moviemanager.util.Log;

/**
 * Manages the persistence of associations between data objects, e.g., between
 * {@link Movie}s and {@link Performer}s. Associations are stored in guava
 * Multimaps.
 *
 * @param <L> keys of the map, e.g. {@link Movie}s.
 * @param <R> values of the map, e.g. {@link Performer}s.
 */
public class AssociationStorageManager<L extends Identifiable,
        R extends Identifiable> {

    // directory in which the associations files are stored:
    // "movie_manager/associations"
    private File directory;

    // file name of the JSON file in that the associations are stored, e.g.
    // "movie-performer".
    private String identifier;
    private Function<Integer, L> leftIdUnmapper;
    private Function<Integer, R> rightIdUnmapper;

    /**
     * Constructor for an AssociationStorageManager for one multimap of
     * associations.
     *
     * @param identifier      file name of the JSON file in that the
     *                        associations are stored, e.g. "movie-performer".
     * @param multimap        {@link Multimap} of data objects, e.g. of {@link
     *                        Movie}s and {@link Performer}s.
     * @param leftIdUnmapper  method to get the key object by its id, e.g.
     *                        getMovieById().
     * @param rightIdUnmapper method to get the value object by its id, e.g.
     *                        getPerformerById().
     */
    public AssociationStorageManager(String identifier,
                                     Function<Integer, L> leftIdUnmapper,
                                     Function<Integer, R> rightIdUnmapper,
                                     File homeDirectory) {
        this.identifier = identifier;
        this.leftIdUnmapper = leftIdUnmapper;
        this.rightIdUnmapper = rightIdUnmapper;
        directory = new File(homeDirectory + File.separator + "associations");
    }

    /**
     * Saves the associations in a JSON file.
     */
    public void saveToStorage(Multimap<L, R> multimap) {
        try {
            List<JSONObject> mappedAssociations = new ArrayList<>();
            for (Entry<L, R> entry : multimap.entries()) {
                if (Objects.nonNull(entry.getValue())) {
                    int idOfLeftObject = entry.getKey().getId();
                    int idOfRightObject = entry.getValue().getId();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("first", idOfLeftObject);
                    jsonObject.put("second", idOfRightObject);
                    mappedAssociations.add(jsonObject);
                }
            }
            if (!mappedAssociations.isEmpty()) {
                JSONArray array = new JSONArray(mappedAssociations);
                List<String> lines = asList(array.toString(2).split("\n"));
                File file = new File(
                        directory + File.separator + identifier + ".json");
                FileUtils.writeLines(file, lines);
            }
        } catch (JSONException | IOException e) {
            Log.e("saveToStorage",
                    "Writing to directory " + directory + " failed.\n" + e);
        }
    }

    /**
     * Loads the associations from a JSON file into a {@link Multimap} object.
     *
     * @return {@link Multimap} of data objects, e.g. {@link Movie}s and {@link
     * Performer}s.
     */
    public Multimap<L, R> loadFromStorage() {
        Multimap<L, R> multimap = HashMultimap.create();
        try {
            List<String> lines = FileUtils.readAllLines(
                    FileUtils.resolve(directory, identifier + ".json"));
            Multimap<Integer, Integer> mapping = loadMappings(lines);
            for (Entry<Integer, Integer> entry : mapping.entries()) {
                L obj1 = leftIdUnmapper.apply(entry.getKey());
                R obj2 = rightIdUnmapper.apply(entry.getValue());
                if (obj1 != null && obj2 != null) {
                    multimap.put(obj1, obj2);
                }
            }
        } catch (JSONException | IOException | NullPointerException | NoSuchElementException e) {
            Log.d("loadFromStorage",
                    "Associations could not be read from file: " + e);
        }
        return multimap;
    }

    /**
     * @param lines of the json file that stores the associations.
     * @return multimap of the guava package. A multimap allows to insert a key
     * more than once. This is important since, for example, a {@link Movie} can
     * be linked to two different {@link Performer}s.
     * @throws JSONException if the json file that stores the associations is
     *                       corrupted.
     */
    Multimap<Integer, Integer> loadMappings(List<String> lines)
            throws JSONException {
        String jsonString = StringUtils.join(lines, "");
        JSONArray array = new JSONArray(jsonString);
        Multimap<Integer, Integer> idMap = HashMultimap.create();
        for (int i = 0; i < array.length(); i++) {
            JSONObject jsonObject = array.getJSONObject(i);
            int idOfLeftObject = jsonObject.getInt("first");
            int idOfRightObject = jsonObject.getInt("second");
            idMap.put(idOfLeftObject, idOfRightObject);
        }
        return idMap;
    }
}

