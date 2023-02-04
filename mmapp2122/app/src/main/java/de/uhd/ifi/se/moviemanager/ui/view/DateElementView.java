package de.uhd.ifi.se.moviemanager.ui.view;

import static de.uhd.ifi.se.moviemanager.util.DateUtils.dateToText;
import static de.uhd.ifi.se.moviemanager.util.DateUtils.textToDate;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorRes;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import de.uhd.ifi.se.moviemanager.R;
import de.uhd.ifi.se.moviemanager.ui.dialog.DateSelectionDialog;
import de.uhd.ifi.se.moviemanager.util.CustomViewUtils;

/**
 * Creates an element in the movie detail view and also in the performer detail
 * view. In the movie detail view, it is responsible for showing and changing
 * the watch date. In the performer detail view, it is responsible for showing
 * and changing the birth date. It is used in
 * {@link de.uhd.ifi.se.moviemanager.ui.detail.MovieDetailActivity}
 * and {@link de.uhd.ifi.se.moviemanager.ui.detail.PerformerDetailActivity}
 * classes to show the watch and birth date, respectively.
 */

public class DateElementView extends FrameLayout {
    private static final int DISABLED_COLOR = R.color.light_gray;

    private TextView showDate;
    private ImageView selectDate;
    private ImageView removeDate;
    private TextView showError;

    private boolean errorEnable = true;
    private boolean editEnable = true;
    private String errorText = "";
    private String dateFormat = "dd.MM.yyyy";
    private SimpleDateFormat formatter;
    private Date date;
    private Date minDate;
    private Date maxDate;

    private BiConsumer<Date, Date> selectListener = (oldDate, newDate) -> {
    };
    private Consumer<Date> removeListener = oldDate -> {
    };

    public DateElementView(Context context) {
        this(context, null);
    }

    public DateElementView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DateElementView(Context context, AttributeSet attrs,
                           int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        initFromAttributes(attrs);
        if (editEnable) {
            inflate(getContext(), R.layout.view_date_selection, this);
            bindEditViews();
            initEditViewContent();
        } else {
            inflate(getContext(), R.layout.view_date_selection_nonedit, this);
            bindViews();
            initViewContent();
        }
    }

    private void initFromAttributes(AttributeSet attrs) {
        if (attrs == null) {
            return;
        }

        TypedArray ta = getContext()
                .obtainStyledAttributes(attrs, R.styleable.DateElementView);
        errorEnable = ta.getBoolean(R.styleable.DateElementView_errorEnable,
                errorEnable);
        editEnable = ta
                .getBoolean(R.styleable.DateElementView_editEnable, editEnable);
        errorText = getString(ta, R.styleable.DateElementView_errorText,
                errorText);
        dateFormat = getString(ta, R.styleable.DateElementView_dateFormat,
                dateFormat);

        formatter = new SimpleDateFormat(dateFormat, Locale.US);

        date = textToDate(formatter,
                getString(ta, R.styleable.DateElementView_date, ""));
        minDate = textToDate(formatter,
                getString(ta, R.styleable.DateElementView_minDate, ""));
        maxDate = textToDate(formatter,
                getString(ta, R.styleable.DateElementView_maxDate, ""));

        ta.recycle();
    }

    private String getString(TypedArray ta, int id, String def) {
        String s = ta.getString(id);
        return s == null ? def : s;
    }

    private void bindEditViews() {
        showDate = findViewById(R.id.show_date);
        selectDate = findViewById(R.id.select_date);
        removeDate = findViewById(R.id.remove_date);
        showError = findViewById(R.id.show_error);
    }

    private void bindViews() {
        showDate = findViewById(R.id.show_date);
        showError = findViewById(R.id.show_error);
    }

    private void initEditViewContent() {
        initShowError();

        selectDate.setOnClickListener(this::onDateSelection);
        removeDate.setOnClickListener(this::onDateRemoval);

        showDate.setText(dateToText(formatter, date));
        setRemoveDateEnabled(date != null);
    }

    private void initShowError() {
        if (!errorEnable) {
            findViewById(R.id.date_end).setVisibility(GONE);
            showError.setVisibility(GONE);
        } else {
            showError.setText(errorText);
        }
    }

    /**
     * Is executed when the user clicks on the calendar icon.
     *
     * @param view calendar icon that the user clicked.
     */
    private void onDateSelection(View view) {
        if (view.getId() != selectDate.getId()) {
            return;
        }
        FragmentManager fm = ((FragmentActivity) getContext())
                .getSupportFragmentManager();
        DateSelectionDialog dialog = DateSelectionDialog
                .create(date, minDate, maxDate);
        dialog.setDateChangeListener(newDate -> {
            if (newDate == null) {
                return;
            }

            setRemoveDateEnabled(true);
            selectListener.accept(date, newDate);
            date = newDate;
            showDate.setText(dateToText(formatter, date));
        });
        dialog.show(fm, "dialog_date_selection");
    }

    /**
     * Set if the bin icon is enabled and clickable.
     *
     * @param enabled Boolean to decide if the Date can be removed.
     */
    private void setRemoveDateEnabled(boolean enabled) {
        setImageViewEnabled(removeDate, enabled, R.color.dark_red);
    }

    /**
     * Set whether an icon is clickable and its colour.
     *
     * @param imageView      an icon in the UI.
     * @param enabled        boolean if the icon is clickable.
     * @param enabledColorId Colour of the icon.
     */
    private void setImageViewEnabled(ImageView imageView, boolean enabled,
                                     @ColorRes int enabledColorId) {
        CustomViewUtils.setImageViewEnabled(imageView, enabled, DISABLED_COLOR,
                enabledColorId);
    }

    /**
     * Is executed when the user clicks on the bin icon.
     *
     * @param view bin icon that the user clicked.
     */
    private void onDateRemoval(View view) {
        if (view.getId() != removeDate.getId()) {
            return;
        }

        setRemoveDateEnabled(false);
        removeListener.accept(date);

        date = null;
        showDate.setText(dateToText(formatter, null));
    }

    private void initViewContent() {
        initShowError();

        showDate.setText(dateToText(formatter, date));
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
        if (removeDate != null) {
            setRemoveDateEnabled(date != null);
        }
        showDate.setText(dateToText(formatter, date));
        invalidate();
    }

    /**
     * Sets a Listener that is notified if the Date is removed.
     *
     * @param removeListener listener to be set.
     */
    private void setRemoveListener(Consumer<Date> removeListener) {
        this.removeListener = removeListener;
    }

    /**
     * Sets a Listener that is notified if a Date is selected.
     *
     * @param selectListener listener to be set.
     */
    private void setSelectListener(BiConsumer<Date, Date> selectListener) {
        this.selectListener = selectListener;
    }

    /**
     * Sets a Listener that is notified if a Date is whanged.
     *
     * @param listener listener to be set.
     */
    public void setDateChangeListener(Consumer<Date> listener) {
        setRemoveListener(listener);
        setSelectListener((oldDate, newDate) -> listener.accept(oldDate));
    }

    public void setErrorText(String msg) {
        showError.setText(msg);
    }

    /**
     * Enables the calendar and bin icons as enabled.
     *
     * @param enabled Boolean to decide whether to enable the icons.
     */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        setShowDateEnabled(enabled);
        setSelectDateEnabled(enabled);
        if (date != null) {
            setRemoveDateEnabled(enabled);
        }
    }

    private void setShowDateEnabled(boolean enabled) {
        showDate.setEnabled(enabled);
        int colorId = DISABLED_COLOR;
        if (enabled) {
            colorId = R.color.black;
        }

        showDate.setTextColor(showDate.getContext().getColor(colorId));
    }

    private void setSelectDateEnabled(boolean enabled) {
        setImageViewEnabled(selectDate, enabled, R.color.colorPrimaryLight);
    }
}


