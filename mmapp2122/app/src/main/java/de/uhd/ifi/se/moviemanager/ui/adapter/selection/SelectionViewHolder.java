package de.uhd.ifi.se.moviemanager.ui.adapter.selection;

import android.view.View;
import android.widget.CompoundButton;
import android.widget.FrameLayout;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import de.uhd.ifi.se.moviemanager.R;
import de.uhd.ifi.se.moviemanager.ui.adapter.base.ContentBinder;
import de.uhd.ifi.se.moviemanager.ui.dialog.SimpleDialog;
import de.uhd.ifi.se.moviemanager.util.BiBooleanConsumer;

class SelectionViewHolder<T> extends ViewHolder {
    private final SelectionAdapter<T> adapter;
    private final CompoundButton selectedMarker;
    private final FrameLayout contentRoot;
    private BiBooleanConsumer<T> listener;
    private final int limit;

    SelectionViewHolder(SelectionAdapter<T> adapter, View item, int limit) {
        super(item);
        item.setTag(this);
        this.adapter = adapter;
        selectedMarker = item.findViewById(R.id.selection_marker);
        contentRoot = item.findViewById(R.id.content);
        this.limit = limit;
    }

    void setOnSelectionChangedListener(BiBooleanConsumer<T> listener) {
        this.listener = listener;
        selectedMarker.setOnCheckedChangeListener(this::onCheckedChange);
    }

    private void onCheckedChange(CompoundButton box, boolean isChecked) {
        if (isChecked && adapter.getSelectedElements()
                .size() >= limit && limit > 0) {
            isChecked = false;
            selectedMarker.setChecked(isChecked);
            SimpleDialog.error(contentRoot.getContext())
                    .setMessage(R.string.error_selection_limit_reached)
                    .setPositiveButtonText(R.string.ok)
                    .setPositiveButtonAction(DialogFragment::dismiss).show();
        }
        if (listener != null && box.getId() == selectedMarker.getId()) {
            onItemClicked(listener, isChecked);
        }
    }

    private void onItemClicked(BiBooleanConsumer<T> listener,
                               boolean isChecked) {
        int position = getBindingAdapterPosition();
        SelectionProxy<T> element = adapter.getElementByPosition(position);
        listener.accept(element.getObj(), isChecked);
        element.setEnabled(isChecked);
    }

    void bindView(int position, ContentBinder<T> binder) {
        SelectionProxy<T> element = adapter.getElementByPosition(position);
        selectedMarker.setOnCheckedChangeListener(null);
        selectedMarker.setChecked(element.isEnabled());
        selectedMarker.setOnCheckedChangeListener(this::onCheckedChange);
        binder.bindViewToElement(contentRoot, element.getObj());
    }

}
