package de.uhd.ifi.se.moviemanager;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;
import static de.uhd.ifi.se.moviemanager.ui.master.DataMasterFragment.newMovieFragmentInstance;
import static de.uhd.ifi.se.moviemanager.ui.master.DataMasterFragment.newPerformerFragmentInstance;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Set;

import de.uhd.ifi.se.moviemanager.model.Movie;
import de.uhd.ifi.se.moviemanager.model.MovieManagerModel;
import de.uhd.ifi.se.moviemanager.model.Performer;
import de.uhd.ifi.se.moviemanager.storage.StorageManagerAccess;
import de.uhd.ifi.se.moviemanager.ui.master.SearchMasterFragment;

/**
 * Main activity of the movie manager app. Run this activity to start the app.
 * The data objects and their associations are managed in the {@link
 * MovieManagerModel} class.
 */
public class MovieManagerActivity extends AppCompatActivity {
    private static final StorageManagerAccess STORAGE = StorageManagerAccess
            .getInstance();
    private MovieManagerModel model = MovieManagerModel.getInstance();

    public static final String STORAGE_NAME = "movie_manager";

    private static final int WRITE_REQUEST_CODE = 0;

    private BottomNavigationView navigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);
        checkPermissionsStateAndSetup();
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
    }

    private void checkPermissionsStateAndSetup() {
        if (STORAGE.openMovieManagerStorage(this)) {
            setupIfPermissionIsGranted();
        } else {
            requestPermissions(STORAGE.getRequiredPermissions(),
                    WRITE_REQUEST_CODE);
        }
    }

    private void setupIfPermissionIsGranted() {
        navigationView = findViewById(R.id.bottom_navigation);
        navigationView.setSelectedItemId(R.id.bottom_navigation_movies);
        openInitialFragment();
        navigationView
                .setOnItemSelectedListener(this::onNavigationItemSelected);
    }

    private void openInitialFragment() {
        Fragment fragment = createMoviesFragment();
        openFragment(fragment, false);
    }

    private Fragment createMoviesFragment() {
        Set<Movie> movies = model.getMovies();
        int menuId = R.string.bottom_navigation_menu_movies;
        return newMovieFragmentInstance(menuId, movies);
    }

    private boolean onNavigationItemSelected(MenuItem menuItem) {
        Fragment fragment = null;
        int itemId = menuItem.getItemId();

        if (itemId == R.id.bottom_navigation_movies) {
            fragment = createMoviesFragment();
        } else if (itemId == R.id.bottom_navigation_performers) {
            fragment = createPerformersFragment();
        } else if (itemId == R.id.bottom_navigation_search) {
            fragment = createSearchFragment();
        }

        if (fragment != null) {
            openFragment(fragment);
        }

        return fragment != null;
    }

    private Fragment createPerformersFragment() {
        Set<Performer> performers = model.getPerformers();
        int menuId = R.string.bottom_navigation_menu_performers;
        return newPerformerFragmentInstance(menuId, performers);
    }

    private Fragment createSearchFragment() {
        int menuId = R.string.bottom_navigation_menu_search;
        return SearchMasterFragment.newInstance(menuId);
    }

    private void openFragment(Fragment fragment) {
        openFragment(fragment, true);
    }

    private void openFragment(Fragment fragment, boolean addToBackStack) {
        FragmentTransaction transaction = getSupportFragmentManager()
                .beginTransaction();
        transaction.replace(R.id.content, fragment);
        if (addToBackStack) {
            transaction.addToBackStack(null);
        } else {
            transaction.disallowAddToBackStack();
        }
        transaction.commit();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions,
                grantResults);
        if (requestCode == WRITE_REQUEST_CODE) {
            if (grantResults[0] == PERMISSION_GRANTED) {
                checkPermissionsStateAndSetup();
            } else {
                finish();
            }
        }
    }

    public void setBottomNavigationTo(@StringRes int nameId) {
        navigationView.setOnItemSelectedListener(null);
        switch (nameId) {
            case R.string.bottom_navigation_menu_movies:
                navigationView.setSelectedItemId(R.id.bottom_navigation_movies);
                break;
            case R.string.bottom_navigation_menu_performers:
                navigationView
                        .setSelectedItemId(R.id.bottom_navigation_performers);
                break;
            case R.string.bottom_navigation_menu_search:
                navigationView.setSelectedItemId(R.id.bottom_navigation_search);
                break;
            default:
                throw new IllegalStateException("Unexpected value: " + nameId);
        }
        navigationView
                .setOnItemSelectedListener(this::onNavigationItemSelected);
    }

}
