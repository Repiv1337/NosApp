package de.uhd.ifi.se.moviemanager.ui.adapter.selection;

public interface MultiSelectionListener<T> {
    void onElementSelected(T element);

    void onElementUnselected(T element);
}
