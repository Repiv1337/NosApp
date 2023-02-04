package de.uhd.ifi.se.moviemanager.model;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Set;

import de.uhd.ifi.se.moviemanager.storage.DataObjectStorageManager;

/**
 * Interface for all objects that have a unique identifier.
 * <p>
 * The {@link DataObjectStorageManager} needs the following Json annotations to store objects that
 * implement the {@link Identifiable} interface in Json files.
 */
@JsonTypeInfo(use = JsonTypeInfo.Id.CLASS, include = JsonTypeInfo.As.PROPERTY, property = "class")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Movie.class),
        @JsonSubTypes.Type(value = Performer.class)
})
public interface Identifiable {

    int getId();

    void setId(int id);

    /**
     * Returns a free id in a sorted set. If there are no gaps, the last id + 1 is returned.
     *
     * @param set
     *         sorted set of {@link Movie}s, {@link Performer}s, {@link Tag}s, {@link Renter}s or
     *         {@link ImagePyramid}s. The objects are sorted by their id. The respective classes
     *         implement the {@link Comparable} interface.
     * @param <T>
     *         class that implements {@link Identifiable} interface, e.g., {@link Movie}, {@link
     *         Performer}, {@link Renter} or {@link ImagePyramid} class.
     * @return first free id in the sorted set. If there are no gaps, the last id + 1 is returned.
     */
    static <T> int findFreeId(Set<T> set) {
        int i = 0;
        for (T object : set) {
            if (((Identifiable) object).getId() > i) {
                return i;
            }
            i++;
        }
        return i;
    }
}
