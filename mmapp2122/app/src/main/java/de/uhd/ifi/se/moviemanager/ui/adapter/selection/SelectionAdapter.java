package de.uhd.ifi.se.moviemanager.ui.adapter.selection;

import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.FrameLayout;

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiPredicate;

import de.uhd.ifi.se.moviemanager.R;
import de.uhd.ifi.se.moviemanager.model.Movie;
import de.uhd.ifi.se.moviemanager.model.Performer;
import de.uhd.ifi.se.moviemanager.ui.adapter.base.BaseFilter;
import de.uhd.ifi.se.moviemanager.ui.adapter.base.ContentBinder;
import de.uhd.ifi.se.moviemanager.ui.adapter.base.DirectFilterable;
import de.uhd.ifi.se.moviemanager.ui.adapter.base.IndexedAdapter;

/**
 * Used in the {@link de.uhd.ifi.se.moviemanager.ui.dialog.ListDialog}, where it
 * is used to show a list of single selectable or multiselectable {@link
 * Movie}s, {@link Performer}s, {@link Tag}s or {@link Renter}s in a {@link
 * RecyclerView}.
 *
 * For example, it is used when linking performers to a movie.
 *
 * @param <T> {@link Movie} or {@link Performer}
 *            class.
 */
public abstract class SelectionAdapter<T>
        extends Adapter<SelectionViewHolder<T>>
        implements DirectFilterable, IndexedAdapter<SelectionProxy<T>> {
    protected final LayoutInflater inflater;
    protected final List<SelectionProxy<T>> data;
    protected final List<SelectionProxy<T>> modifiedData;
    @LayoutRes
    protected final int elementLayoutId;
    protected final BiPredicate<SelectionProxy<T>, CharSequence> filterCriterion;
    protected final Comparator<SelectionProxy<T>> orderCriterion;
    protected final ContentBinder<T> binder;
    protected final int limit;
    @LayoutRes
    private int wrapperLayoutId = R.layout.listitem_selectable;

    protected SelectionAdapter(Context context, @LayoutRes int elementLayoutId,
                               List<T> data,
                               BiPredicate<T, CharSequence> filterCriterion,
                               Comparator<T> orderCriterion,
                               ContentBinder<T> binder, int limit) {
        inflater = LayoutInflater.from(context);
        this.elementLayoutId = elementLayoutId;
        this.data = data.stream().map(SelectionProxy::new).collect(toList());
        modifiedData = new ArrayList<>();

        this.filterCriterion = (proxy, seq) -> filterCriterion
                .test(proxy.getObj(), seq);
        this.orderCriterion = comparing(SelectionProxy::getObj, orderCriterion);
        this.binder = binder;

        this.limit = limit;

        updateWithFilteredData(this.data);
    }

    private void updateWithFilteredData(List<SelectionProxy<T>> filtered) {
        modifiedData.clear();
        modifiedData.addAll(filtered);
        modifiedData.sort(orderCriterion);
        notifyDataSetChanged();
    }

    void setWrapperLayoutId(@LayoutRes int wrapperLayoutId) {
        this.wrapperLayoutId = wrapperLayoutId;
    }

    @NonNull
    @Override
    public SelectionViewHolder<T> onCreateViewHolder(@NonNull ViewGroup parent,
                                                     int viewType) {
        View view = inflater.inflate(wrapperLayoutId, parent, false);
        FrameLayout root = view.findViewById(R.id.content);
        inflater.inflate(elementLayoutId, root, true);
        SelectionViewHolder<T> holder = new SelectionViewHolder<>(this, view,
                limit);
        holder.setOnSelectionChangedListener(this::onSelectionChanged);
        return holder;
    }

    protected abstract void onSelectionChanged(T element, boolean isSelected);

    @Override
    public void onBindViewHolder(@NonNull SelectionViewHolder<T> holder,
                                 int position) {
        holder.bindView(position, binder);
    }

    @Override
    public int getItemCount() {
        return modifiedData.size();
    }

    @Override
    public SelectionProxy<T> getElementByPosition(int position) {
        return modifiedData.get(position);
    }

    @Override
    public Filter getFilter() {
        return new BaseFilter<>(data, filterCriterion,
                this::updateWithFilteredData);
    }

    public List<T> getSelectedElements() {
        return modifiedData.stream().filter(SelectionProxy::isEnabled)
                .map(SelectionProxy::getObj).collect(toList());
    }

    @Override
    public void filter(CharSequence constraint) {
        getFilter().filter(constraint);
    }
}


