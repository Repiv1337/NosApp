package de.uhd.ifi.se.moviemanager.ui.adapter.base;

import android.view.ViewGroup;

@FunctionalInterface
public interface ContentBinder<T> {
    void bindViewToElement(ViewGroup parent, T element);
}
