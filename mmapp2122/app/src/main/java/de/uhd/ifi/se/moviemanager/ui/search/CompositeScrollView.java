package de.uhd.ifi.se.moviemanager.ui.search;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;

import java.util.function.BiConsumer;

/**
 * Class enables scrolling within the blocks of the search master view. Used in
 * the {@link de.uhd.ifi.se.moviemanager.ui.master.SearchMasterFragment}.
 */
public class CompositeScrollView extends ScrollView {
    private BiConsumer<View, MotionEvent> dispatchListener;

    public CompositeScrollView(Context context) {
        super(context);
    }

    public CompositeScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CompositeScrollView(Context context, AttributeSet attrs,
                               int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setOnDispatchListener(Runnable dispatchListener) {
        setOnDispatchListener((view, ev) -> dispatchListener.run());
    }

    private void setOnDispatchListener(BiConsumer<View, MotionEvent> listener) {
        dispatchListener = listener;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (dispatchListener != null) {
            dispatchListener.accept(this, ev);
        }
        return super.dispatchTouchEvent(ev);
    }
}
