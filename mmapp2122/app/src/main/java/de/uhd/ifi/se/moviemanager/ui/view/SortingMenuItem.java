package de.uhd.ifi.se.moviemanager.ui.view;

import android.os.Parcelable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import de.uhd.ifi.se.moviemanager.R;
import de.uhd.ifi.se.moviemanager.model.Identifiable;
import de.uhd.ifi.se.moviemanager.model.ImageBased;
import de.uhd.ifi.se.moviemanager.model.Movie;
import de.uhd.ifi.se.moviemanager.model.Nameable;
import de.uhd.ifi.se.moviemanager.ui.master.comparator.CategorizedComparator;

/**
 * Creates an entry in the sorting menu, e.g. to sort the list of {@link Movie}s
 * according to their title or watch date.
 *
 * @param <T> depends on the objects that should be sorted, e.g. {@link Movie}
 *            or {@link de.uhd.ifi.se.moviemanager.model.Performer}.
 */
public class SortingMenuItem<T extends Identifiable & Nameable & ImageBased & Parcelable> {
    private int sortingMenuItemResource;
    private String title;
    private CategorizedComparator<T> categorizedComparator;

    /**
     * Creates a sorting menu item that is disabled by default.
     *
     * @param title                 name of the sorting criterion.
     * @param categorizedComparator class that is responsible for categorized
     *                              sorting of data objects.
     */
    public SortingMenuItem(String title,
                           CategorizedComparator<T> categorizedComparator) {
        this.title = title;
        this.categorizedComparator = categorizedComparator;
        setNeutral();
    }

    /**
     * Creates a sorting menu item that is enabled by default.
     *
     * @param title                 name of the sorting criterion.
     * @param categorizedComparator class that is responsible for categorized
     *                              sorting of data objects.
     * @param isDescending          true if sorting should be done in descending
     *                              direction.
     */
    public SortingMenuItem(String title,
                           CategorizedComparator<T> categorizedComparator,
                           boolean isDescending) {
        this(title, categorizedComparator);
        setState(false, !isDescending);
    }

    /**
     * Disables this sorting criterion.
     */
    public void setNeutral() {
        setState(true, false);
    }

    /**
     * @param isNeutral    true if the sorting criterion is disabled.
     * @param isDescending true if the sorting criterion should be enabled and
     *                     sorting should be done in descending direction. Is
     *                     ignored, if isNeutral is set to true.
     */
    public void setState(boolean isNeutral, boolean isDescending) {
        if (isNeutral) {
            sortingMenuItemResource = R.drawable.ic_master_order_neutral;
        } else if (isDescending) {
            sortingMenuItemResource = R.drawable.ic_master_order_desc;
        } else {
            sortingMenuItemResource = R.drawable.ic_master_order_asc;
        }
    }

    /**
     * Activates the sorting criterion. If the criterion was not active before,
     * the sorting direction is set to descending. If the criterion was already
     * activated, the direction (ascending or descending) is flipped.
     *
     * @return id of the icon resource.
     */
    public int activate() {
        if (isDescending()) {
            sortingMenuItemResource = R.drawable.ic_master_order_asc;
        } else {
            sortingMenuItemResource = R.drawable.ic_master_order_desc;
        }
        return sortingMenuItemResource;
    }

    /**
     * @return true if the sorting direction is currently set to descending.
     * False, if ascending or neutral.
     */
    public boolean isDescending() {
        return sortingMenuItemResource == R.drawable.ic_master_order_desc;
    }

    /**
     * @return true if the sorting criterion is currently disabled. False, if
     * ascending or descending.
     */
    public boolean isNeutral() {
        return sortingMenuItemResource == R.drawable.ic_master_order_neutral;
    }

    /**
     * @param root sorting menu that contains this sorting criterion as well as
     *             other sorting criteria.
     */
    public void updateView(View root) {
        TextView titleView = root.findViewById(R.id.title);
        ImageView imageView = root.findViewById(R.id.order_state);

        titleView.setText(title);
        imageView.setImageResource(sortingMenuItemResource);
    }

    /**
     * @return class that is responsible for categorized sorting of data
     * objects. This class needs to implement the {@link CategorizedComparator}
     * interface.
     */
    public CategorizedComparator<T> getCategorizedComparator() {
        return categorizedComparator;
    }

    /**
     * @return id of the XML resource that indicates whether the sorting
     * criterion is disabled (neutral), descending, or ascending.
     */
    public int getSortingMenuItemResource() {
        return sortingMenuItemResource;
    }
}
