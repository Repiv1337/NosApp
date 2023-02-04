package de.uhd.ifi.se.moviemanager.ui.search;

import android.app.Activity;
import android.view.View;

import androidx.annotation.IdRes;
import androidx.constraintlayout.widget.ConstraintLayout;

import de.uhd.ifi.se.moviemanager.R;

/**
 * Class manages image setting and text loading into views related to searching
 * in {@link DataSearchActivity} and in
 * {@link de.uhd.ifi.se.moviemanager.ui.master.SearchMasterFragment}.
 */
public class SearchInfo {
    private final ConstraintLayout anchor;

    public SearchInfo(Activity context) {
        anchor = context.findViewById(R.id.search_info);
    }

    /**
     * @param visibility of the search info, e.g. {@link View#VISIBLE}.
     */
    public void setVisibility(int visibility) {
        anchor.setVisibility(visibility);
    }

    /**
     * Use to add the link to "Search only in ...".
     *
     * @param id     of the link that should be clickable.
     * @param action navigation action.
     */
    public void addOnClickActionTo(@IdRes int id, Runnable action) {
        View view = anchor.findViewById(id);
        if (view != null) {
            view.setOnClickListener(v -> action.run());
        }
    }
}
