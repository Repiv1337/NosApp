package de.uhd.ifi.se.moviemanager.util;

import static android.content.Context.INPUT_METHOD_SERVICE;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public final class AndroidUtils {

    // private constructor to prevent instantiation
    private AndroidUtils() {
        throw new UnsupportedOperationException();
    }

    public static void closeKeyboard(final Activity context) {
        closeKeyboard(context, context.getCurrentFocus());
    }

    public static void closeKeyboard(final Context context,
                                     final View focusedView) {
        if (focusedView != null) {
            focusedView.clearFocus();
            final Object systemService = context
                    .getSystemService(INPUT_METHOD_SERVICE);
            final InputMethodManager imm = (InputMethodManager) systemService;
            imm.hideSoftInputFromWindow(focusedView.getWindowToken(), 0);
        }
    }
}
