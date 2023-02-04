package de.uhd.ifi.se.moviemanager.ui.master.comparator;

import static java.lang.Math.abs;

import java.util.Date;

import de.uhd.ifi.se.moviemanager.model.Movie;
import de.uhd.ifi.se.moviemanager.util.DateUtils;

/**
 * Compares two {@link Movie}s by their watch date.
 *
 * @see Movie#getWatchDate()
 */
public class WatchDateComparator implements CategorizedComparator<Movie> {

    @Override
    public int compare(Movie movie1, Movie movie2) {
        if (movie1.getWatchDate() == null) {
            // movie cannot be compared since watch date is null
            return -42;
        }
        if (movie2.getWatchDate() == null) {
            // movie cannot be compared since watch date is null
            return 42;
        }
        return movie1.getWatchDate().compareTo(movie2.getWatchDate());
    }

    @Override
    public String getCategoryNameFor(Movie movie) {
        Date watchDate = movie.getWatchDate();
        if (watchDate == null) {
            return "Never";
        }
        Date now = DateUtils.nowAtMidnight();
        long diff = abs(DateUtils.differenceInDays(now,
                DateUtils.normDateTimeToMidnight(watchDate)));
        return getCategoryNameFor(diff);
    }

    private static String getCategoryNameFor(long diff) {
        if (diff == 0) {
            return "Today";
        } else if (diff == 1) {
            return "Yesterday";
        } else if (diff <= 7) {
            return "Last Week";
        } else if (diff <= 30) {
            return "Last Month";
        } else if (diff <= 365) {
            return "Last Year";
        } else {
            return "A Long Time Ago";
        }
    }

    @Override
    public String getSubText(Movie movie) {
        Date watchDate = movie.getWatchDate();
        String subTitle;
        if (watchDate == null) {
            subTitle = "Not watched";
        } else {
            subTitle = DateUtils.dateToText(watchDate);
        }
        return subTitle;
    }
}