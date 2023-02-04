package de.uhd.ifi.se.moviemanager.util;

import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.ColorRes;

public final class CustomViewUtils {

    // private constructor to prevent instantiation
    private CustomViewUtils() {
        throw new UnsupportedOperationException();
    }

    public static void setImageViewEnabled(final ImageView imageView,
                                           boolean enabled,
                                           @ColorRes int disabledColorId,
                                           @ColorRes int enabledColorId) {
        imageView.setEnabled(enabled);
        int colorId = disabledColorId;

        if (enabled) {
            colorId = enabledColorId;
        }

        applyColorFilter(imageView, colorId);
    }

    public static void applyColorFilter(final ImageView imageView,
                                        @ColorRes int colorId) {
        imageView.setColorFilter(imageView.getContext().getColor(colorId));
    }

    public static void setImageViewBackgroundColor(final ImageView imageView,
                                                   boolean enabled,
                                                   @ColorRes int disabledColorId,
                                                   @ColorRes int enabledColorId) {
        imageView.setEnabled(enabled);
        int colorId = enabled ? enabledColorId : disabledColorId;

        imageView.setBackgroundColor(imageView.getContext().getColor(colorId));
    }

    public static int parseInt(final TextView textView, int defaultValue) {
        final String input = textView.getText().toString();
        int result;
        if (input.isEmpty()) {
            result = defaultValue;
        } else {
            result = Integer.parseInt(input);
        }
        return result;
    }


    public static String intToString(int number, int noValue) {
        String result;
        if (number > noValue) {
            result = Integer.toString(number);
        } else {
            result = "";
        }
        return result;
    }
}
