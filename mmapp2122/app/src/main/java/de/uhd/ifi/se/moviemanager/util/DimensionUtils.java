package de.uhd.ifi.se.moviemanager.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

public final class DimensionUtils {

    // private constructor to prevent instantiation
    private DimensionUtils() {
        throw new UnsupportedOperationException();
    }

    public static float dpToPixels(Context context, int dp) {
        Resources r = context.getResources();
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                r.getDisplayMetrics());
    }
}
