package de.uhd.ifi.se.moviemanager.ui.master.comparator;

import de.uhd.ifi.se.moviemanager.model.Movie;
import de.uhd.ifi.se.moviemanager.model.Rateable;

/**
 * Compares two {@link Movie}s by their rating.
 *
 * @see Movie#calculateOverallRating()
 * @see Movie#getOverallRatingInStars()
 */
public class OverallRatingComparator implements CategorizedComparator<Movie> {

    @Override
    public int compare(Movie movie1, Movie movie2) {
        return ((Double) movie1.getOverallRating())
                .compareTo(movie2.getOverallRating());
    }

    @Override
    public String getCategoryNameFor(Movie movie) {
        return Rateable.ratingToText(movie.getOverallRating(), false);
    }

    @Override
    public String getSubText(Movie movie) {
        return movie.getOverallRatingInStars();
    }
}