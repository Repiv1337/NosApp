package de.uhd.ifi.se.moviemanager.ui.master.comparator;

import de.uhd.ifi.se.moviemanager.model.Performer;
import de.uhd.ifi.se.moviemanager.util.DateUtils;

/**
 * Compares two {@link Performer}s by their age. Categorises the age by steps of
 * 10 years.
 *
 * @see Performer#getAge()
 */
public class AgeComparator implements CategorizedComparator<Performer> {

    @Override
    public int compare(Performer performer1, Performer performer2) {
        return ((Integer) performer1.getAge()).compareTo(performer2.getAge());
    }

    @Override
    public String getCategoryNameFor(Performer performer) {
        int category = 10 * (performer.getAge() / 10);
        return Integer.toString(category);
    }

    @Override
    public String getSubText(Performer performer) {
        return DateUtils.dateToText(performer.getDateOfBirth());
    }
}