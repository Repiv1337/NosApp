package de.uhd.ifi.se.moviemanager.ui.adapter.selection;

import android.content.Context;

import androidx.annotation.LayoutRes;

import java.util.Comparator;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.Consumer;

import de.uhd.ifi.se.moviemanager.R;
import de.uhd.ifi.se.moviemanager.model.Movie;
import de.uhd.ifi.se.moviemanager.model.Performer;
import de.uhd.ifi.se.moviemanager.ui.adapter.base.ContentBinder;

/**
 * Builder class to create the {@link SelectionAdapter}, uses the Builder design
 * pattern. The Builder design pattern allows us to write readable,
 * understandable code to set up complex objects.
 *
 * @param <T> {@link Movie} or {@link Performer}
 *            class.
 */
public class SelectionAdapterBuilder<T> {
    private final Context context;

    // button type - influences the selection type
    @LayoutRes
    private int wrapperLayoutId;
    @LayoutRes
    private int elementLayoutId;
    private List<T> data;

    private BiPredicate<T, CharSequence> filterCriterion;
    private Comparator<T> orderCriterion;
    private ContentBinder<T> binder;

    private Consumer<SelectionChangedContext<T>> onSelectionChanged;

    private int limit = -1;

    SelectionAdapterBuilder(Context context) {
        this.context = context;
        useCheckBox();
    }

    // used for multiple selection
    SelectionAdapterBuilder<T> useCheckBox() {
        wrapperLayoutId = R.layout.listitem_selectable;
        return this;
    }

    // used for single selection
    // radio buttons provide mutually exclusive selection values
    SelectionAdapterBuilder<T> useRadioButton() {
        wrapperLayoutId = R.layout.listitem_one_selectable;
        return this;
    }

    public SelectionAdapterBuilder<T> setElementLayoutId(
            @LayoutRes int elementLayoutId) {
        this.elementLayoutId = elementLayoutId;
        return this;
    }

    public SelectionAdapterBuilder<T> setData(List<T> data) {
        this.data = data;
        return this;
    }

    public SelectionAdapterBuilder<T> setFilterCriterion(
            BiPredicate<T, CharSequence> filterCriterion) {
        this.filterCriterion = filterCriterion;
        return this;
    }

    public SelectionAdapterBuilder<T> setOrderCriterion(
            Comparator<T> orderCriterion) {
        this.orderCriterion = orderCriterion;
        return this;
    }

    public SelectionAdapterBuilder<T> setContentBinder(
            ContentBinder<T> binder) {
        this.binder = binder;
        return this;
    }

    public SelectionAdapterBuilder<T> setSelectionLimit(int limit) {
        this.limit = limit;
        return this;
    }

    SelectionAdapterBuilder<T> setOnSelectionChanged(
            Consumer<SelectionChangedContext<T>> onSelectionChanged) {
        this.onSelectionChanged = onSelectionChanged;
        return this;
    }

    public SelectionAdapter<T> build() {
        SelectionAdapter<T> result = new SelectionAdapter<T>(context,
                elementLayoutId, data, filterCriterion, orderCriterion, binder,
                limit) {
            @Override
            protected void onSelectionChanged(T element, boolean isSelected) {
                SelectionChangedContext<T> changedContext =
                        new SelectionChangedContext<>(
                        this, data, element, isSelected);
                onSelectionChanged.accept(changedContext);
            }
        };
        result.setWrapperLayoutId(wrapperLayoutId);
        return result;
    }
}
