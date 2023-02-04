package de.uhd.ifi.se.moviemanager.ui.adapter;

import static java.util.Collections.unmodifiableList;
import static java.util.Objects.requireNonNull;

import android.content.Context;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import java.util.List;

import de.uhd.ifi.se.moviemanager.R;
import de.uhd.ifi.se.moviemanager.model.Identifiable;
import de.uhd.ifi.se.moviemanager.model.ImageBased;
import de.uhd.ifi.se.moviemanager.model.Nameable;
import de.uhd.ifi.se.moviemanager.model.Performer;
import de.uhd.ifi.se.moviemanager.ui.view.SortingMenuItem;

/**
 * Enables to show the list of sorting criteria, for example "Name", "Rating",
 * "Age" in {@link Performer} MasterView (i.e.
 * {@link de.uhd.ifi.se.moviemanager.ui.master.PerformerMasterFragment}).
 * <p>
 * Used in the {@link de.uhd.ifi.se.moviemanager.ui.master.DataMasterFragment}s.
 */
public class SortingCriteriaAdapter<T extends Identifiable & Nameable & ImageBased & Parcelable>
        extends BaseAdapter {
    private final LayoutInflater inflater;
    private final List<SortingMenuItem<T>> items;

    public SortingCriteriaAdapter(Context context,
                                  List<SortingMenuItem<T>> items) {
        inflater = LayoutInflater.from(requireNonNull(context));
        this.items = unmodifiableList(requireNonNull(items));
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public SortingMenuItem getItem(int i) {
        return items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if (view == null) {
            view = inflater.inflate(R.layout.menu_item_sort_order, null);
            viewHolder = new ViewHolder(view) {
            };
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        getItem(i).updateView(viewHolder.itemView);

        return view;
    }
}