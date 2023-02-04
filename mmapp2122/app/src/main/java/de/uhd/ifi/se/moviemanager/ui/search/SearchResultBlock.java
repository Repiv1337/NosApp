package de.uhd.ifi.se.moviemanager.ui.search;

import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.StringRes;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import de.uhd.ifi.se.moviemanager.R;
import de.uhd.ifi.se.moviemanager.util.AndroidUtils;

/**
 * The search master view shows movies, performers, and renters matching the
 * search query in separate blocks. This class is responsible for creating such
 * blocks.
 */
public class SearchResultBlock extends FrameLayout {
    private Context context;
    private TextView resultName;
    private RecyclerView resultList;
    private Button continueButton;
    private ImageView continueImage;

    public SearchResultBlock(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        inflate(context, R.layout.view_search_result_block, this);

        bindViews();
    }

    private void bindViews() {
        resultName = findViewById(R.id.result_name);
        resultList = findViewById(R.id.model_objects_with_image);
        continueButton = findViewById(R.id.continue_button);
        continueImage = findViewById(R.id.continue_image);

        resultList.setLayoutManager(createLinearLayoutManager());
    }

    private LinearLayoutManager createLinearLayoutManager() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                getContext());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        return linearLayoutManager;
    }

    public void setName(@StringRes int nameId) {
        setName(context.getString(nameId));
    }

    private void setName(String name) {
        resultName.setText(name);
    }

    public void setAdapter(Adapter<ViewHolder> listAdapter) {
        resultList.setAdapter(listAdapter);
    }

    public void setContinueText(@StringRes int textId) {
        setContinueText(context.getString(textId));
    }

    private void setContinueText(String text) {
        continueButton.setText(text);
    }

    public void setContinueListener(OnClickListener listener) {
        continueButton.setOnClickListener(listener);
        continueImage.setOnClickListener(listener);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        AndroidUtils.closeKeyboard((Activity) context);
        return super.onInterceptTouchEvent(ev);
    }
}
