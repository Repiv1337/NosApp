package de.uhd.ifi.se.moviemanager.ui.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import java.util.function.Consumer;

import de.uhd.ifi.se.moviemanager.R;

/**
 * Simple dialog with a title, message, ok/confirm button, and cancel button.
 *
 * The Builder design pattern is used to create the dialog, use {@link
 * #warning(Context)} or {@link #error(Context)}.
 */
public class SimpleDialog extends DialogFragment {

    private String title;
    private String message;
    private String positiveText;
    private String negativeText;
    private boolean isNegativeButtonEnabled;

    private TextView titleView;
    private TextView messageView;
    private Button positiveButton;
    private Button negativeButton;

    private Consumer<DialogFragment> onPositive;
    private Consumer<DialogFragment> onNegative;

    private SimpleDialog() {
    }

    /**
     * Method to build the {@link SimpleDialog}, uses the Builder design
     * pattern.
     *
     * @param context Android activity that the simple dialog should be shown
     *                in.
     * @return {@link SimpleDialogBuilder} object that can be further extended,
     * e.g. with methods {@link SimpleDialogBuilder#setMessage(String)}. To show
     * the dialog, use {@link SimpleDialogBuilder#show()}.
     */
    public static SimpleDialogBuilder warning(Context context) {
        return new SimpleDialogBuilder(context).setTitle(R.string.warning);
    }

    public static SimpleDialogBuilder error(Context context) {
        return new SimpleDialogBuilder(context).setTitle(R.string.error)
                .disableNegative().setCancelable(false);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_simple, container);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        titleView = view.findViewById(R.id.dialog_title);
        messageView = view.findViewById(R.id.dialog_message);
        positiveButton = view.findViewById(R.id.positive_button);
        negativeButton = view.findViewById(R.id.negative_button);

        setTitle(title);
        setMessage(message);
        setPositiveText(positiveText);
        setNegativeText(negativeText);
        setNegativeButtonEnabled(isNegativeButtonEnabled);
        beginListening();
    }

    private void beginListening() {
        positiveButton.setOnClickListener(view -> {
            if (onPositive != null) {
                onPositive.accept(this);
            }
        });
        negativeButton.setOnClickListener(view -> {
            if (onNegative != null) {
                onNegative.accept(this);
            }
        });
    }

    private void setTitle(String title) {
        titleView.setText(title);
    }

    private void setMessage(String message) {
        messageView.setText(message);
    }

    private void setPositiveText(String positiveText) {
        positiveButton.setText(positiveText);
    }

    private void setNegativeText(String negativeText) {
        negativeButton.setText(negativeText);
    }

    private void setNegativeButtonEnabled(boolean isButtonEnabled) {
        if (!isButtonEnabled) {
            negativeButton.setVisibility(View.GONE);
        }
    }

    /**
     * Builder class to create a {@link SimpleDialog}, uses the Builder design
     * pattern. The Builder design pattern allows us to write readable,
     * understandable code to set up complex objects.
     */
    public static class SimpleDialogBuilder {
        private final Context context;
        private String title;
        private String message;
        private String positiveButtonText;
        private String negativeButtonText;

        private boolean disableNegative;
        private boolean isCancelable;

        private Consumer<DialogFragment> onPositive;
        private Consumer<DialogFragment> onNegative;

        private SimpleDialogBuilder(Context context) {
            this.context = context;
            disableNegative = false;
            isCancelable = true;

            positiveButtonText = context.getString(R.string.confirm);
            negativeButtonText = context.getString(R.string.cancel);

            onNegative = DialogFragment::dismiss;
        }

        public SimpleDialogBuilder setTitle(@StringRes int titleId) {
            title = context.getString(titleId);
            return this;
        }

        public SimpleDialogBuilder setTitle(String title) {
            this.title = title;
            return this;
        }

        public SimpleDialogBuilder setMessage(String message) {
            this.message = message;
            return this;
        }

        public SimpleDialogBuilder setMessage(@StringRes int messageId) {
            message = context.getString(messageId);
            return this;
        }

        public SimpleDialogBuilder setMessage(@StringRes int messageId,
                                              String... args) {
            message = String
                    .format(context.getString(messageId), (Object[]) args);
            return this;
        }

        public SimpleDialogBuilder disableNegative() {
            disableNegative = true;
            return this;
        }

        public SimpleDialogBuilder setCancelable(boolean isCancelable) {
            this.isCancelable = isCancelable;
            return this;
        }

        public SimpleDialogBuilder setPositiveButtonText(
                @StringRes int textId) {
            positiveButtonText = context.getString(textId);
            return this;
        }

        public SimpleDialogBuilder setPositiveButtonAction(
                Consumer<DialogFragment> action) {
            onPositive = action;
            return this;
        }

        public SimpleDialogBuilder setNegativeButtonText(
                @StringRes int textId) {
            negativeButtonText = context.getString(textId);
            return this;
        }

        public SimpleDialogBuilder setNegativeButtonAction(
                Consumer<DialogFragment> action) {
            onNegative = action;
            return this;
        }

        /**
         * Shows the {@link SimpleDialog}.
         */
        public void show() {
            FragmentManager manager = ((FragmentActivity) context)
                    .getSupportFragmentManager();
            SimpleDialog dialog = new SimpleDialog();
            dialog.title = title;
            dialog.message = message;
            dialog.positiveText = positiveButtonText;
            dialog.onPositive = onPositive;
            dialog.negativeText = negativeButtonText;
            dialog.onNegative = onNegative;
            dialog.setCancelable(isCancelable);
            dialog.isNegativeButtonEnabled = !disableNegative;
            dialog.show(manager, "simple_dialog");
        }
    }
}
