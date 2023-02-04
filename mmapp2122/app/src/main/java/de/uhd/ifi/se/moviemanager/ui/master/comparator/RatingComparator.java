package de.uhd.ifi.se.moviemanager.ui.master.comparator;

import de.uhd.ifi.se.moviemanager.model.Movie;
import de.uhd.ifi.se.moviemanager.model.Rateable;

/**
 * Compares two objects of the same class (e.g. two {@link Movie}s) by their
 * individual rating.
 *
 * @param <T> class that implements the {@link Rateable} interface, e.g. {@link
 *            Movie} or {@link de.uhd.ifi.se.moviemanager.model.Performer}.
 * @see Rateable#getRating()
 * @see Rateable#getRatingInStars()
 */
public class RatingComparator<T extends Rateable>
        implements CategorizedComparator<T> {

    @Override
    public int compare(T object1, T object2) {
        return ((Double) object1.getRating()).compareTo(object2.getRating());
    }

    @Override
    public String getCategoryNameFor(T object) {
        if (object.getRatingInStars().isEmpty()) {
            return "#";
        }
        return object.getRatingInStars();
    }

    @Override
    public String getSubText(T object) {
        return object.getRatingInStarsWithNumber();
    }
}