package de.uhd.ifi.se.moviemanager.ui.wiki;

import android.content.Intent;

/**
 * Defines which pages are queried from wikipedia: either performers or movies.
 */
public enum WikiQueryMode {
    UNDEFINED, PERFORMER, MOVIE;

    /**
     * @param intent passed when the user moves from one Android activity to
     *               another activity.
     * @param field  constant to identify an extra field of the intent.
     * @return {@link WikiQueryMode}.
     */
    public static WikiQueryMode getFromIntent(Intent intent, String field) {
        int defaultOrdinal = WikiQueryMode.UNDEFINED.ordinal();
        int ordinal = intent.getIntExtra(field, defaultOrdinal);
        return WikiQueryMode.values()[ordinal];
    }
}
