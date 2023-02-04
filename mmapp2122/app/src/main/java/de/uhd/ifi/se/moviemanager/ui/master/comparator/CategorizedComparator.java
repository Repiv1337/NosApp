package de.uhd.ifi.se.moviemanager.ui.master.comparator;

import java.util.Comparator;

import de.uhd.ifi.se.moviemanager.model.Movie;

/**
 * Compares two objects of the same class by a certain criterion and categorizes
 * them. Extends the {@link Comparator} interface. You need to implement this
 * interface if you want to add a new sorting criterion to the sorting menu.
 *
 * @param <T> e.g. {@link Movie} or
 * {@link de.uhd.ifi.se.moviemanager.model.Performer}.
 */
public interface CategorizedComparator<T> extends Comparator<T> {

    /**
     * @param object that is shown in the sorted list, e.g. {@link Movie}
     *               object.
     * @return category that the object falls into, e.g. a certain letter (for
     * alphabetically sorting), rating, or age.
     */
    String getCategoryNameFor(T object);

    /**
     * @param object that is shown in the sorted list, e.g. {@link Movie}
     *               object.
     * @return text that is shown underneath the title/name of the object.
     */
    String getSubText(T object);
}