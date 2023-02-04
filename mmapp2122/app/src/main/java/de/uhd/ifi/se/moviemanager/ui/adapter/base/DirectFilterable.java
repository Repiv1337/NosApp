package de.uhd.ifi.se.moviemanager.ui.adapter.base;

import android.widget.Filterable;

public interface DirectFilterable extends Filterable {
    void filter(final CharSequence constraint);
}
