package de.uhd.ifi.se.moviemanager.model;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.Locale;

/**
 * Interface for all objects that can be rated (e.g. {@link Movie}s) .
 */
public interface Rateable {

    /**
     * @return number between -1 and 5. -1 means unrated. 5 is the highest
     * rating.
     */
    double getRating();

    /**
     * @return true if rated.
     */
    @JsonIgnore
    default boolean isRated() {
        return getRating() >= 0;
    }

    /**
     * @param rating number between -1 and 5. -1 means unrated. 5 is the highest
     *               rating.
     */
    void setRating(double rating);

    /**
     * @return String with stars that represent the rating or "Not Rated" if
     * unrated.
     */
    @JsonIgnore
    default String getRatingInStars() {
        return ratingToText(getRating(), false);
    }

    /**
     * @return String with stars that represent the rating AND the rating as a
     * double number or "Not Rated" if unrated.
     */
    @JsonIgnore
    default String getRatingInStarsWithNumber() {
        return ratingToText(getRating(), true);
    }

    /**
     * @param value         rating in a double number
     * @param isNumberShown true if also the double value is shown.
     * @return String with stars that represent the rating or "Not Rated" if
     * unrated.
     */
    static String ratingToText(double value, boolean isNumberShown) {
        String sub;
        if (value < 0) {
            sub = "Not Rated";
        } else {
            sub = textRatingBar(value, 5);
            if (isNumberShown) {
                sub += String.format(Locale.US, " (%2.1f)", value);
            }
        }
        return sub;
    }

    static String textRatingBar(double d, int maxStars) {
        StringBuilder builder = new StringBuilder(maxStars);
        for (int i = 0; i < maxStars; ++i) {
            builder.append(selectStar(d, i));
        }

        return builder.toString();
    }

    static String selectStar(double value, int offset) {
        if (value < 0.5 + offset) {
            return "\u2606";
        } else if (value < 0.9 + offset) {
            return "\u272F";
        } else {
            return "\u2605";
        }
    }
}