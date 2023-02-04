package de.uhd.ifi.se.moviemanager.util;

import android.app.Activity;
import android.text.Editable;
import android.text.TextWatcher;

import androidx.appcompat.widget.SearchView.OnQueryTextListener;

import java.util.function.Consumer;
import java.util.function.Predicate;

/**
 * Class of objects that get attached to editables (editable views). Their
 * methods are called when the user triggers a view (an editable) by interacting
 * with it, for example by changing the text.
 */
public final class Listeners {

    // private constructor to prevent instantiation
    private Listeners() {
        throw new UnsupportedOperationException();
    }

    public static TextWatcher createOnTextChangedListenerChar(
            Consumer<CharSequence> action) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // no action to perform
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // no action to perform
            }

            @Override
            public void afterTextChanged(Editable s) {
                action.accept(s);
            }
        };
    }

    public static TextWatcher createOnTextChangedListener(
            Consumer<Boolean> action) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // no action to perform
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // no action to perform
            }

            @Override
            public void afterTextChanged(Editable s) {
                action.accept(true);
            }
        };
    }

    public static OnQueryTextListener liveQueryListener(Activity activity,
                                                        Predicate<String> onQueryChange) {
        return new OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                AndroidUtils.closeKeyboard(activity);
                return onQueryTextChange(query);
            }

            @Override
            public boolean onQueryTextChange(String query) {
                return onQueryChange.test(query);
            }
        };
    }

    public static OnQueryTextListener submitQueryListener(Activity activity,
                                                          Predicate<String> onQuerySubmit) {
        return new OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                AndroidUtils.closeKeyboard(activity);
                return onQuerySubmit.test(query);
            }

            @Override
            public boolean onQueryTextChange(String query) {
                return false;
            }
        };
    }
}
