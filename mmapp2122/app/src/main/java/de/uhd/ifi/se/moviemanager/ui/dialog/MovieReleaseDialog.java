package de.uhd.ifi.se.moviemanager.ui.dialog;

import static java.util.Optional.ofNullable;
import static de.uhd.ifi.se.moviemanager.util.AndroidUtils.closeKeyboard;

import android.app.Dialog;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputLayout;

import java.util.Calendar;
import java.util.Date;
import java.util.function.Consumer;

import de.uhd.ifi.se.moviemanager.R;
import de.uhd.ifi.se.moviemanager.model.MovieRelease;
import de.uhd.ifi.se.moviemanager.util.AndroidUtils;
import de.uhd.ifi.se.moviemanager.util.DateUtils;
import de.uhd.ifi.se.moviemanager.util.Month;

/**
 * Dialog for adding or changing a {@link MovieRelease}. A {@link MovieRelease}
 * consists of the location and the date.
 *
 * Use {@link #create()} to create a new {@link MovieRelease}. Use {@link
 * #create(MovieRelease release)} to change/modify an existing {@link
 * MovieRelease}.
 */
public class MovieReleaseDialog extends DialogFragment
        implements OnClickListener, OnValueChangeListener {
    private static final int MIN_DAY = 1;
    private static final int MAX_DAY = 31;
    private static final int MIN_MONTH = 0;
    private static final int MAX_MONTH = 11;
    private static final int MIN_YEAR = 1895;
    private static final int MAX_YEAR = 2100;

    private TextView showTitle;
    private NumberPicker dayPicker;
    private NumberPicker monthPicker;
    private NumberPicker yearPicker;
    private TextInputLayout editName;
    private Button confirmRelease;
    private Button cancelRelease;

    private MovieRelease release;
    private Consumer<MovieRelease> confirmationListener;

    private MovieReleaseDialog() {

    }

    public static MovieReleaseDialog create() {
        return create(null);
    }

    public static MovieReleaseDialog create(MovieRelease release) {
        MovieReleaseDialog movieReleaseDialog = new MovieReleaseDialog();
        movieReleaseDialog.release = release;
        return movieReleaseDialog;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_create_release, container);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        showTitle = view.findViewById(R.id.dialog_title);
        dayPicker = view.findViewById(R.id.edit_release_day);
        monthPicker = view.findViewById(R.id.edit_release_month);
        yearPicker = view.findViewById(R.id.edit_release_year);
        editName = view.findViewById(R.id.edit_release_name);
        confirmRelease = view.findViewById(R.id.positive_button);
        cancelRelease = view.findViewById(R.id.negative_button);

        initUI();
        setConfirmReleaseEnabled(!getLocation().isEmpty());
        beginListening();
    }

    private void initUI() {
        if (release == null) {
            showTitle.setText(getContext().getString(R.string.create_release));
        } else {
            showTitle.setText(getContext().getString(R.string.modify_release));
        }

        ofNullable(editName.getEditText()).ifPresent(
                e -> e.setText(release == null ? "" : release.getLocation()));

        dayPicker.setMinValue(MIN_DAY);
        dayPicker.setMaxValue(MAX_DAY);

        monthPicker.setMinValue(MIN_MONTH);
        monthPicker.setMaxValue(MAX_MONTH);
        monthPicker.setDisplayedValues(Month.asStrings());

        yearPicker.setMinValue(MIN_YEAR);
        yearPicker.setMaxValue(MAX_YEAR);
        yearPicker.setWrapSelectorWheel(false);

        setInitialPickerStates();

        confirmRelease.setEnabled(!getLocation().isEmpty());
    }

    private void setInitialPickerStates() {
        Date initialDate = release == null ? DateUtils.now() : release
                .getDate();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(initialDate);
        dayPicker.setValue(calendar.get(Calendar.DAY_OF_MONTH));
        monthPicker.setValue(calendar.get(Calendar.MONTH));
        yearPicker.setValue(calendar.get(Calendar.YEAR));
    }

    private String getLocation() {
        return ofNullable(editName.getEditText()).map(EditText::getText)
                .map(Object::toString).map(String::trim).orElse("");
    }

    private void checkNameConstraints() {
        boolean emptyName = getLocation().isEmpty();
        if (emptyName) {
            editName.setError(getContext()
                    .getString(R.string.missing_attribute_place_of_release));
        } else {
            editName.setError(null);
        }

        setConfirmReleaseEnabled(!emptyName);
    }

    private void setConfirmReleaseEnabled(boolean enabled) {
        if (enabled) {
            confirmRelease.setTextColor(getContext().getColor(R.color.white));
        } else {
            confirmRelease
                    .setTextColor(getContext().getColor(R.color.lighter_gray));
        }

        confirmRelease.setEnabled(enabled);
    }

    private void beginListening() {
        ofNullable(editName.getEditText())
                .ifPresent(e -> e.setOnFocusChangeListener((v, hasFocus) -> {
                    if (!hasFocus) {
                        checkNameConstraints();
                    }
                }));
        ofNullable(editName.getEditText()).ifPresent(
                e -> e.setOnEditorActionListener((v, actionId, event) -> {
                    if (event == null || event
                            .getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                        closeKeyboard(getContext(), editName);
                        return true;
                    }
                    return false;
                }));

        dayPicker.setOnValueChangedListener(this);
        monthPicker.setOnValueChangedListener(this);
        yearPicker.setOnValueChangedListener(this);

        confirmRelease.setOnClickListener(this);
        cancelRelease.setOnClickListener(this);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        return new Dialog(getContext()) {
            @Override
            public boolean dispatchTouchEvent(@NonNull MotionEvent event) {
                checkNameConstraints();
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    View v = getCurrentFocus();
                    if (v instanceof EditText) {
                        Rect outRect = new Rect();
                        v.getGlobalVisibleRect(outRect);
                        if (!outRect.contains((int) event.getRawX(),
                                (int) event.getRawY())) {
                            AndroidUtils.closeKeyboard(getContext(), v);
                        }
                    }
                }
                return super.dispatchTouchEvent(event);
            }
        };
    }

    public void setConfirmationListener(Consumer<MovieRelease> listener) {
        confirmationListener = listener;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.positive_button) {
            if (confirmationListener != null) {
                MovieRelease movieRelease = new MovieRelease(getLocation(),
                        getDate());
                confirmationListener.accept(movieRelease);
            }
            dismiss();
        } else if (view.getId() == R.id.negative_button) {
            dismiss();
        }
    }

    private Date getDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(yearPicker.getValue(), monthPicker.getValue(),
                dayPicker.getValue(), 0, 0, 0);
        return calendar.getTime();
    }

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        switch (picker.getId()) {
            case R.id.edit_release_day:
                break;
            case R.id.edit_release_month:
            case R.id.edit_release_year:
                updateDays();
                break;
            default:
                // do nothing
        }
    }

    private void updateDays() {
        int month = monthPicker.getValue();
        int year = yearPicker.getValue();
        int monthMaxDays = Month.values()[month].getMaxDaysWithoutLeap();
        if (month == Month.FEBRUARY.ordinal()) {
            monthMaxDays += DateUtils.isLeapYear(year) ? 1 : 0;
        }
        dayPicker.setMaxValue(monthMaxDays);
    }
}
