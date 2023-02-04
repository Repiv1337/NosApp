package de.uhd.ifi.se.moviemanager.ui.master.comparator;

import java.util.function.Function;

import de.uhd.ifi.se.moviemanager.model.Movie;
import de.uhd.ifi.se.moviemanager.model.Nameable;

/**
 * Compares two objects of the same class (e.g. two {@link Movie}s) by their
 * name and sorts them alphabetically.
 *
 * @param <T> class that implements the {@link Nameable} interface, e.g. {@link
 *            Movie} or {@link de.uhd.ifi.se.moviemanager.model.Performer}.
 */
public class NameComparator<T extends Nameable>
        implements CategorizedComparator<T> {

    private final Function<T, String> getMetaInfo;

    /**
     * @param getMetaInfo getter method of the object that returns a String. The
     *                    String is used as the info text for the sorted items.
     *                    For example, when sorting {@link Movie}s by their
     *                    title, the info text will show their overall rating.
     */
    public NameComparator(Function<T, String> getMetaInfo) {
        this.getMetaInfo = getMetaInfo;
    }
    
    @Override
    public int compare(T object1, T object2) {
        return object1.getName().compareToIgnoreCase(object2.getName());
    }

    @Override
    public String getCategoryNameFor(T object) {
        if (object.getName().isEmpty()) {
            return "#";
        }
        // the first letter will be a category, e.g. A, B, C, ...
        return object.getName().substring(0, 1).toUpperCase();
    }

    @Override
    public String getSubText(T object) {
        return getMetaInfo.apply(object);
    }
}