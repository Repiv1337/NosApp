package de.uhd.ifi.se.moviemanager.ui.master;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import de.uhd.ifi.se.moviemanager.R;

/**
 * Shows the categories in the MasterView, e.g. A, B, C or rating stars. Is used
 * in the {@link de.uhd.ifi.se.moviemanager.ui.adapter.DataRVAdapter}.
 */
public class HeaderViewHolder extends RecyclerView.ViewHolder {
    private final TextView categoryView;

    public HeaderViewHolder(@NonNull View itemView) {
        super(itemView);
        categoryView = itemView.findViewById(R.id.categoryView);
    }

    /**
     * @param categoryText category that the sorted objects fall into, e.g. a
     *                     certain letter (for alphabetically sorting), rating,
     *                     or age.
     */
    public void setCategoryText(String categoryText) {
        categoryView.setText(categoryText);
    }
}
