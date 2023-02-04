package de.uhd.ifi.se.moviemanager.ui.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.function.Consumer;

import de.uhd.ifi.se.moviemanager.R;
import de.uhd.ifi.se.moviemanager.util.DateUtils;

/**
 * Dialog to select a date, e.g. the movie watch date.
 *
 * Use {@link #create(Date initial, Date minDate, Date maxDate)} or {@link
 * #createFromTodayAndOnward()} to create the {@link DateSelectionDialog}.
 */
public class DateSelectionDialog extends DialogFragment {
    private final Calendar calendar;
    private Date minDate;
    private Date maxDate;

    private Consumer<Date> dateChangeListener = d -> {
    };

    private DatePicker picker;
    private Button positiveButton;
    private Button negativeButton;

    private DateSelectionDialog() {
        calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    public static DateSelectionDialog createFromTodayAndOnward() {
        Date today = DateUtils.nowAtMidnight();
        return create(today, today, null);
    }

    public static DateSelectionDialog create(Date initial, Date minDate,
                                             Date maxDate) {
        DateSelectionDialog dialog = new DateSelectionDialog();
        if (initial == null) {
            dialog.calendar.setTime(DateUtils.nowAtMidnight());
        } else {
            dialog.calendar.setTime(initial);
        }
        dialog.minDate = minDate;
        dialog.maxDate = maxDate;
        return dialog;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_date_selection, container);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        picker = view.findViewById(R.id.calendar);
        positiveButton = view.findViewById(R.id.positive_button);
        negativeButton = view.findViewById(R.id.negative_button);

        setupUI();
    }

    private void setupUI() {
        if (minDate != null) {
            picker.setMinDate(minDate.getTime());
        }
        if (maxDate != null) {
            picker.setMaxDate(maxDate.getTime());
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        picker.updateDate(year, month, day);

        positiveButton.setOnClickListener(this::onButtonClick);
        negativeButton.setOnClickListener(this::onButtonClick);
    }

    private void onButtonClick(View view) {
        int id = view.getId();

        if (id == R.id.positive_button) {
            dateChangeListener.accept(getDateFromDatePicker(picker));
            dismiss();
        } else if (id == R.id.negative_button) {
            dateChangeListener.accept(null);
            dismiss();
        }
    }

    private Date getDateFromDatePicker(DatePicker datePicker) {
        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();

        calendar.set(year, month, day, 0, 0, 0);

        return calendar.getTime();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme()) {
            @Override
            public void onBackPressed() {
                onButtonClick(negativeButton);
                dismiss();
            }
        };
    }

    public void setDateChangeListener(@NonNull Consumer<Date> listener) {
        dateChangeListener = listener;
    }
}