package de.uhd.ifi.se.moviemanager.ui.adapter.base;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.IdRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import java.util.Comparator;
import java.util.List;

import de.uhd.ifi.se.moviemanager.model.Movie;
import de.uhd.ifi.se.moviemanager.model.Performer;

/**
 * Enables to show a list of {@link Performer}s in a {@link RecyclerView}. Used
 * in the {@link de.uhd.ifi.se.moviemanager.ui.dialog.ListDialog} to create the
 * {@link de.uhd.ifi.se.moviemanager.ui.dialog.PerformerSafeRemovalDialog}.
 *
 * @param <T> {@link Movie} or {@link Performer}
 *            class.
 */
public class SimpleAdapter<T> extends Adapter<SimpleViewHolder<T>>
        implements IndexedAdapter<T> {
    protected final LayoutInflater inflater;
    protected List<T> modelObjects;

    @LayoutRes
    private final int elementLayoutId;
    @IdRes
    private final int rootId;

    private final Comparator<T> orderCriterion;
    private final ContentBinder<T> binder;

    public SimpleAdapter(Context context, @LayoutRes int elementLayoutId,
                         @IdRes int rootId, List<T> modelObjects,
                         Comparator<T> orderCriterion,
                         ContentBinder<T> binder) {
        inflater = LayoutInflater.from(context);
        this.elementLayoutId = elementLayoutId;
        this.rootId = rootId;
        this.modelObjects = modelObjects;
        this.orderCriterion = orderCriterion;
        this.binder = binder;
        updateWithFilteredData(modelObjects);
    }

    private void updateWithFilteredData(List<T> filtered) {
        if (orderCriterion != null) {
            filtered.sort(orderCriterion);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public SimpleViewHolder<T> onCreateViewHolder(@NonNull ViewGroup parent,
                                                  int viewType) {
        View view = inflater.inflate(elementLayoutId, parent, false);
        return new SimpleViewHolder<>(this, view, rootId);
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleViewHolder<T> holder,
                                 int position) {
        holder.bindView(position, binder);
    }

    @Override
    public int getItemCount() {
        return modelObjects.size();
    }

    @Override
    public T getElementByPosition(int position) {
        return modelObjects.get(position);
    }
}